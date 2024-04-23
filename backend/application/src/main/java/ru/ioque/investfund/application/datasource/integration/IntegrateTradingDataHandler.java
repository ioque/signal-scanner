package ru.ioque.investfund.application.datasource.integration;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.application.datasource.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.application.integration.event.TradingDataIntegrated;
import ru.ioque.investfund.application.integration.event.TradingStateChanged;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateTradingDataHandler extends IntegrationHandler<IntegrateTradingDataCommand> {
    UUIDProvider uuidProvider;
    DatasourceRepository datasourceRepository;
    IntradayValueRepository intradayValueRepository;
    EventPublisher eventPublisher;

    public IntegrateTradingDataHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        IntradayValueRepository intradayValueRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider, datasourceProvider);
        this.uuidProvider = uuidProvider;
        this.datasourceRepository = datasourceRepository;
        this.intradayValueRepository = intradayValueRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void businessProcess(IntegrateTradingDataCommand command) {
        final UUID integrationSessionMark = uuidProvider.generate();
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        final ExecutorService service = Executors.newCachedThreadPool();
        for (Instrument instrument : datasource.getUpdatableInstruments()) {
            service.execute(() -> integrateTradingDataFor(instrument, datasource, integrationSessionMark));
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new IntegrationProcessingException(e);
        }
        datasourceRepository.save(datasource);
        eventPublisher.publish(TradingDataIntegrated.builder()
            .id(uuidProvider.generate())
            .datasourceId(datasource.getId().getUuid())
            .integrationSessionMark(integrationSessionMark)
            .createdAt(dateTimeProvider.nowDateTime())
            .build());
    }

    private void integrateTradingDataFor(Instrument instrument, Datasource datasource, UUID integrationSessionMark) {
        final TreeSet<IntradayData> intradayData = new TreeSet<>(datasourceProvider.fetchIntradayValues(
            datasource,
            instrument
        ).stream().map(IntradayDataDto::toIntradayValue).toList());
        final TreeSet<AggregatedHistory> aggregateHistories = new TreeSet<>(datasourceProvider.fetchAggregateHistory(
            datasource,
            instrument
        ).stream().map(AggregatedHistoryDto::toAggregateHistory).toList());
        intradayValueRepository.saveAll(intradayData
            .stream()
            .filter(data -> data.getNumber() > instrument.getLastTradingNumber())
            .toList());
        instrument.updateAggregateHistory(aggregateHistories);
        if (instrument.updateTradingState(intradayData)) {
            eventPublisher.publish(
                TradingStateChanged.builder()
                    .id(uuidProvider.generate())
                    .instrumentId(instrument.getId().getUuid())
                    .integrationSessionMark(integrationSessionMark)
                    .createdAt(dateTimeProvider.nowDateTime())
                    .build()
            );
        }
    }
}
