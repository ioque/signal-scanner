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
import ru.ioque.investfund.application.api.command.Result;
import ru.ioque.investfund.application.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.application.datasource.event.TradingDataIntegrated;
import ru.ioque.investfund.application.datasource.event.TradingStateChanged;
import ru.ioque.investfund.application.datasource.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.TradingState;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateTradingDataHandler extends IntegrationHandler<IntegrateTradingDataCommand> {
    DatasourceRepository datasourceRepository;
    IntradayValueRepository intradayValueRepository;
    EventPublisher eventPublisher;

    public IntegrateTradingDataHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        IntradayValueRepository intradayValueRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider, datasourceProvider);
        this.datasourceRepository = datasourceRepository;
        this.intradayValueRepository = intradayValueRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected Result businessProcess(IntegrateTradingDataCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        final ExecutorService service = Executors.newCachedThreadPool();
        for (Instrument instrument : datasource.getUpdatableInstruments()) {
            service.execute(() -> integrateTradingDataFor(instrument, datasource));
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new IntegrationProcessingException(e);
        }
        datasourceRepository.save(datasource);
        eventPublisher.publish(TradingDataIntegrated.builder()
            .datasourceId(datasource.getId().getUuid())
            .createdAt(dateTimeProvider.nowDateTime())
            .build());
        return Result.success();
    }

    private void integrateTradingDataFor(Instrument instrument, Datasource datasource) {
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
                    .instrumentId(instrument.getId().getUuid())
                    .price(instrument.getTradingState().map(TradingState::getTodayLastPrice).orElse(null))
                    .value(instrument.getTradingState().map(TradingState::getTodayValue).orElse(null))
                    .createdAt(dateTimeProvider.nowDateTime())
                    .build()
            );
        }
    }
}
