package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.datasource.value.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayBatch;

import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateTradingDataProcessor extends CommandProcessor<IntegrateTradingDataCommand> {
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    DatasourceProvider datasourceProvider;
    DatasourceRepository repository;
    EventPublisher eventPublisher;

    public IntegrateTradingDataProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.dateTimeProvider = dateTimeProvider;
        this.datasourceProvider = datasourceProvider;
        this.repository = datasourceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void handleFor(IntegrateTradingDataCommand command) {
        final Datasource datasource = getDatasource(command.getDatasourceId());
        datasource.getUpdatableInstruments().forEach(instrument -> {
            final HistoryBatch history = datasourceProvider.fetchHistoryBy(datasource, instrument);
            final IntradayBatch intraday = datasourceProvider.fetchIntradayValuesBy(datasource, instrument);
            intraday.getLastNumber().ifPresent(instrument::updateLastTradingNumber);
            history.getLastDate().ifPresent(instrument::updateLastHistoryDate);
            repository.saveHistoryValues(history.getUniqueValues());
            repository.saveIntradayValues(intraday.getUniqueValues());
        });
        repository.saveDatasource(datasource);
        eventPublisher.publish(TradingDataUpdatedEvent.builder()
            .id(uuidProvider.generate())
            .dateTime(dateTimeProvider.nowDateTime())
            .datasourceId(datasource.getId())
            .build());
    }

    private Datasource getDatasource(UUID datasourceId) {
        return repository
            .getBy(datasourceId)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", datasourceId)
                )
            );
    }
}
