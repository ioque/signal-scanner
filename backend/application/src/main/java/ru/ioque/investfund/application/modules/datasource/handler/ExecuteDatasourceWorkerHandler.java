package ru.ioque.investfund.application.modules.datasource.handler;

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
import ru.ioque.investfund.application.integration.event.TradingDataIntegrated;
import ru.ioque.investfund.application.integration.event.TradingStateChanged;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.DatasourceWorkerManager;
import ru.ioque.investfund.application.modules.datasource.command.ExecuteDatasourceWorker;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.TradingState;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExecuteDatasourceWorkerHandler extends CommandHandler<ExecuteDatasourceWorker> {
    DatasourceProvider datasourceProvider;
    DatasourceWorkerManager datasourceWorkerManager;
    DatasourceRepository datasourceRepository;
    IntradayValueRepository intradayValueRepository;
    EventPublisher eventPublisher;

    public ExecuteDatasourceWorkerHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        DatasourceWorkerManager datasourceWorkerManager,
        DatasourceRepository datasourceRepository,
        IntradayValueRepository intradayValueRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.datasourceWorkerManager = datasourceWorkerManager;
        this.datasourceRepository = datasourceRepository;
        this.intradayValueRepository = intradayValueRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected Result businessProcess(ExecuteDatasourceWorker command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        datasourceWorkerManager.runWorkers(datasource.getId());
        //TODO нижележащий код будет удален по мере рефакторинга
        final ExecutorService service = Executors.newCachedThreadPool();
        for (Instrument instrument : datasource.getUpdatableInstruments()) {
            service.execute(() -> integrateTradingDataFor(instrument, datasource));
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        datasourceRepository.save(datasource);
        eventPublisher.publish(TradingDataIntegrated.builder()
            .datasourceId(datasource.getId().getUuid())
            .createdAt(dateTimeProvider.nowDateTime())
            .build());
        return Result.success();
    }

    private void integrateTradingDataFor(Instrument instrument, Datasource datasource) {
        final TreeSet<IntradayData> intradayData =
            new TreeSet<>(
                datasourceProvider.fetchIntradayValues(datasource, instrument)
                    .stream()
                    .filter(data -> data.getNumber() > instrument.getLastTradingNumber())
                    .toList()
            );
        intradayValueRepository.saveAll(intradayData);
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
