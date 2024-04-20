package ru.ioque.investfund.application.datasource;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.CommandHandler;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.integration.event.TradingDataIntegratedEvent;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.util.TreeSet;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateTradingDataHandler extends CommandHandler<IntegrateTradingDataCommand> {
    UUIDProvider uuidProvider;
    DatasourceProvider datasourceProvider;
    DatasourceRepository datasourceRepository;
    HistoryValueRepository historyValueRepository;
    IntradayValueRepository intradayValueRepository;
    EventPublisher eventPublisher;

    public IntegrateTradingDataHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        HistoryValueRepository historyValueRepository,
        IntradayValueRepository intradayValueRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.datasourceProvider = datasourceProvider;
        this.datasourceRepository = datasourceRepository;
        this.historyValueRepository = historyValueRepository;
        this.intradayValueRepository = intradayValueRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void businessProcess(IntegrateTradingDataCommand command) {
        final Datasource datasource = datasourceRepository.getById(command.getDatasourceId());
        datasource.getUpdatableInstruments().forEach(instrument -> {
            final TreeSet<@Valid HistoryValue> history = datasourceProvider.fetchHistoryBy(datasource, instrument);
            final TreeSet<@Valid IntradayValue> intraday = datasourceProvider.fetchIntradayValuesBy(datasource, instrument);
            validate(history);
            validate(intraday);
            historyValueRepository.saveAll(history.stream().distinct().toList());
            intradayValueRepository.saveAll(intraday.stream().distinct().toList());
            instrument.updateTradingState(intraday);
            instrument.updateAggregateHistory(history);
        });
        datasourceRepository.save(datasource);
        eventPublisher.publish(TradingDataIntegratedEvent.builder()
            .id(uuidProvider.generate())
            .datasourceId(datasource.getId().getUuid())
            .dateTime(dateTimeProvider.nowDateTime())
            .updatedCount(datasource.getUpdatableInstruments().size())
            .build());
    }
}
