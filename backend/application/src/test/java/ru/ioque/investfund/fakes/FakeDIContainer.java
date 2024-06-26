package ru.ioque.investfund.fakes;

import java.util.List;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.handler.CreateDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.DisableUpdateInstrumentHandler;
import ru.ioque.investfund.application.modules.datasource.handler.EnableUpdateInstrumentHandler;
import ru.ioque.investfund.application.modules.datasource.handler.UpdateAggregatedTotalsHandler;
import ru.ioque.investfund.application.modules.datasource.handler.PublishIntradayDataHandler;
import ru.ioque.investfund.application.modules.datasource.handler.RemoveDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.SynchronizeDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.UpdateDatasourceHandler;
import ru.ioque.investfund.application.modules.pipeline.PipelineManager;
import ru.ioque.investfund.application.modules.pipeline.subscriber.RiskManager;
import ru.ioque.investfund.application.modules.pipeline.subscriber.SignalRegistry;
import ru.ioque.investfund.application.modules.pipeline.subscriber.SignalRegistryContext;
import ru.ioque.investfund.application.modules.pipeline.transformer.PerformanceCalculator;
import ru.ioque.investfund.application.modules.pipeline.transformer.PerformanceCalculatorContext;
import ru.ioque.investfund.application.modules.pipeline.transformer.SignalsFinder;
import ru.ioque.investfund.application.modules.pipeline.transformer.SignalsFinderContext;
import ru.ioque.investfund.application.modules.scanner.handler.ActivateScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.CreateScannerCommandHandler;
import ru.ioque.investfund.application.modules.scanner.handler.DeactivateScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.RemoveScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.UpdateScannerCommandHandler;
import ru.ioque.investfund.application.modules.telegrambot.handler.SubscribeHandler;
import ru.ioque.investfund.application.modules.telegrambot.handler.UnsubscribeHandler;
import ru.ioque.investfund.fakes.repository.FakeAggregatedTotalsRepository;
import ru.ioque.investfund.fakes.repository.FakeDatasourceRepository;
import ru.ioque.investfund.fakes.repository.FakeInstrumentRepository;
import ru.ioque.investfund.fakes.repository.FakeIntradayDataJournal;
import ru.ioque.investfund.fakes.repository.FakeScannerRepository;
import ru.ioque.investfund.fakes.repository.FakeSignalRepository;
import ru.ioque.investfund.fakes.repository.FakeTelegramChatRepository;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FakeDIContainer {

    DatasourceStorage datasourceStorage;
    CommandBus commandBus;
    Validator validator;
    FakeDateTimeProvider dateTimeProvider;
    FakeDatasourceProvider exchangeProvider;
    FakeLoggerProvider loggerProvider;
    FakeTelegramMessageSender telegramMessageSender;

    FakeIntradayDataJournal intradayDataJournal;
    FakeSignalRepository signalRepository;
    FakeAggregatedTotalsRepository aggregatedTotalsJournal;
    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    FakeInstrumentRepository instrumentRepository;
    FakeTelegramChatRepository telegramChatRepository;

    DisableUpdateInstrumentHandler disableUpdateInstrumentHandler;
    EnableUpdateInstrumentHandler enableUpdateInstrumentHandler;
    SynchronizeDatasourceHandler synchronizeDatasourceHandler;
    PublishIntradayDataHandler publishIntradayDataHandler;
    CreateDatasourceHandler createDatasourceHandler;
    RemoveDatasourceHandler removeDatasourceHandler;
    UpdateDatasourceHandler updateDatasourceHandler;
    CreateScannerCommandHandler createScannerHandler;
    UpdateScannerCommandHandler updateScannerHandler;
    RemoveScannerHandler removeScannerHandler;
    SubscribeHandler subscribeHandler;
    UnsubscribeHandler unsubscribeHandler;
    DeactivateScannerHandler deactivateScannerHandler;
    ActivateScannerHandler activateScannerHandler;
    UpdateAggregatedTotalsHandler updateAggregatedTotalsHandler;

    PipelineManager pipelineManager;
    PerformanceCalculator performanceCalculator;
    PerformanceCalculatorContext performanceCalculatorContext;
    SignalsFinder signalsFinder;
    SignalsFinderContext signalsFinderContext;
    SignalRegistry signalRegistry;
    SignalRegistryContext signalRegistryContext;
    RiskManager riskManager;

    public FakeDIContainer() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        dateTimeProvider = new FakeDateTimeProvider();
        datasourceStorage = new DatasourceStorage();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        telegramMessageSender = new FakeTelegramMessageSender();

        aggregatedTotalsJournal = new FakeAggregatedTotalsRepository();
        intradayDataJournal = new FakeIntradayDataJournal();
        signalRepository = new FakeSignalRepository();
        datasourceRepository = new FakeDatasourceRepository();
        instrumentRepository = new FakeInstrumentRepository(datasourceRepository);
        scannerRepository = new FakeScannerRepository();
        telegramChatRepository = new FakeTelegramChatRepository();

        updateAggregatedTotalsHandler = new UpdateAggregatedTotalsHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            datasourceRepository,
            aggregatedTotalsJournal
        );
        deactivateScannerHandler = new DeactivateScannerHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository
        );
        activateScannerHandler = new ActivateScannerHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository
        );
        removeScannerHandler = new RemoveScannerHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            signalRepository
        );
        disableUpdateInstrumentHandler = new DisableUpdateInstrumentHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        enableUpdateInstrumentHandler = new EnableUpdateInstrumentHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        synchronizeDatasourceHandler = new SynchronizeDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            instrumentRepository,
            datasourceRepository
        );
        publishIntradayDataHandler = new PublishIntradayDataHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            intradayDataJournal,
            datasourceRepository
        );
        createDatasourceHandler = new CreateDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        removeDatasourceHandler = new RemoveDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        updateDatasourceHandler = new UpdateDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        createScannerHandler = new CreateScannerCommandHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            datasourceRepository
        );
        updateScannerHandler = new UpdateScannerCommandHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            datasourceRepository
        );
        subscribeHandler = new SubscribeHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            telegramChatRepository,
            telegramMessageSender
        );
        unsubscribeHandler = new UnsubscribeHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            telegramChatRepository,
            telegramMessageSender
        );

        commandBus = new CommandBus(
            List.of(
                disableUpdateInstrumentHandler,
                enableUpdateInstrumentHandler,
                synchronizeDatasourceHandler,
                publishIntradayDataHandler,
                createDatasourceHandler,
                updateDatasourceHandler,
                removeDatasourceHandler,
                createScannerHandler,
                updateScannerHandler,
                subscribeHandler,
                unsubscribeHandler,
                removeScannerHandler,
                deactivateScannerHandler,
                activateScannerHandler,
                updateAggregatedTotalsHandler
            )
        );

        performanceCalculatorContext = new PerformanceCalculatorContext();
        signalsFinderContext = new SignalsFinderContext();
        signalRegistryContext = new SignalRegistryContext();
        performanceCalculator = new PerformanceCalculator(performanceCalculatorContext);
        signalsFinder = new SignalsFinder(signalsFinderContext);
        signalRegistry = new SignalRegistry(signalRegistryContext, signalRepository);
        riskManager = new RiskManager(signalRegistryContext, signalRepository);

        pipelineManager = new PipelineManager(
            signalRepository,
            scannerRepository,
            aggregatedTotalsJournal,
            datasourceRepository,
            signalRegistryContext,
            performanceCalculatorContext,
            signalsFinderContext,
            intradayDataJournal,
            performanceCalculator,
            signalsFinder,
            signalRegistry,
            riskManager
        );
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setDatasourceStorage(datasourceStorage);
        return fakeExchangeProvider;
    }
}