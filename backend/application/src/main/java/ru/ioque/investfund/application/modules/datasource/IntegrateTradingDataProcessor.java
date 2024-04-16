package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.datasource.value.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayBatch;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateTradingDataProcessor extends DatasourceProcessor<IntegrateTradingDataCommand> {
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    DatasourceProvider datasourceProvider;
    HistoryValueRepository historyValueRepository;
    IntradayValueRepository intradayValueRepository;
    EventPublisher eventPublisher;

    public IntegrateTradingDataProcessor(
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
        super(dateTimeProvider, validator, loggerProvider, datasourceRepository);
        this.uuidProvider = uuidProvider;
        this.dateTimeProvider = dateTimeProvider;
        this.datasourceProvider = datasourceProvider;
        this.historyValueRepository = historyValueRepository;
        this.intradayValueRepository = intradayValueRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void handleFor(IntegrateTradingDataCommand command) {
        final Datasource datasource = getDatasource(command.getDatasourceId());
        datasource.getUpdatableInstruments().forEach(instrument -> {
            final HistoryBatch history = datasourceProvider.fetchHistoryBy(datasource, instrument);
            final IntradayBatch intraday = datasourceProvider.fetchIntradayValuesBy(datasource, instrument);
            historyValueRepository.saveAll(history.getUniqueValues());
            intradayValueRepository.saveAll(intraday.getUniqueValues());
            instrument.recalcSummary(history, intraday);
        });
        datasourceRepository.save(datasource);
        eventPublisher.publish(TradingDataUpdatedEvent.builder()
            .id(uuidProvider.generate())
            .dateTime(dateTimeProvider.nowDateTime())
            .datasourceId(datasource.getId())
            .build());
    }
}
