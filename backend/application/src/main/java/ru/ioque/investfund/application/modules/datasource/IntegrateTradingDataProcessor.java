package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateTradingDataProcessor extends CommandProcessor<IntegrateTradingDataCommand> {
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    DatasourceProvider datasourceProvider;
    DatasourceRepository repository;
    EventPublisher eventPublisher;

    public IntegrateTradingDataProcessor(
        Validator validator,
        LoggerFacade loggerFacade,
        UUIDProvider uuidProvider,
        DateTimeProvider dateTimeProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        EventPublisher eventPublisher
    ) {
        super(validator, loggerFacade);
        this.uuidProvider = uuidProvider;
        this.dateTimeProvider = dateTimeProvider;
        this.datasourceProvider = datasourceProvider;
        this.repository = datasourceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void handleFor(IntegrateTradingDataCommand command) {
        final Datasource datasource = repository
            .getBy(command.getDatasourceId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", command.getDatasourceId())
                )
            );
        datasource.getUpdatableInstruments().forEach(instrument -> {
            loggerFacade.logRunUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
            final List<HistoryValue> history = datasourceProvider
                .fetchHistoryBy(datasource, instrument)
                .stream()
                .distinct()
                .toList();
            final List<IntradayValue> intraday = datasourceProvider
                .fetchIntradayValuesBy(datasource, instrument)
                .stream()
                .distinct()
                .toList();
            instrument.updateLastTradingNumber(intraday);
            instrument.updateLastHistoryDate(history);
            repository.saveHistoryValues(history);
            repository.saveIntradayValues(intraday);
            loggerFacade.logFinishUpdateMarketData(instrument, dateTimeProvider.nowDateTime());
        });
        repository.saveDatasource(datasource);
        eventPublisher.publish(TradingDataUpdatedEvent.builder()
            .id(uuidProvider.generate())
            .dateTime(dateTimeProvider.nowDateTime())
            .datasourceId(datasource.getId())
            .build());
    }
}
