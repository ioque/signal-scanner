package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.handler.PublishAggregatedHistoryHandler;
import ru.ioque.investfund.application.modules.datasource.handler.DisableUpdateInstrumentHandler;
import ru.ioque.investfund.application.modules.datasource.handler.EnableUpdateInstrumentHandler;
import ru.ioque.investfund.application.modules.datasource.handler.CreateDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.RemoveDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.UpdateDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.SynchronizeDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.PublishIntradayDataHandler;
import ru.ioque.investfund.application.modules.pipeline.PipelineManager;
import ru.ioque.investfund.application.modules.pipeline.sink.RiskManagerSink;
import ru.ioque.investfund.application.modules.pipeline.sink.SignalRegistryContext;
import ru.ioque.investfund.application.modules.pipeline.sink.SignalRegistry;
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
import ru.ioque.investfund.fakes.journal.FakeAggregatedTotalsRepository;
import ru.ioque.investfund.fakes.journal.FakeIntradayJournal;
import ru.ioque.investfund.fakes.journal.FakeSignalJournal;

import java.util.List;

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

    FakeSignalJournal signalJournal;
    FakeIntradayJournal intradayJournal;
    FakeAggregatedTotalsRepository aggregatedTotalsJournal;
    FakeEmulatedPositionRepository emulatedPositionJournal;
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
    PublishAggregatedHistoryHandler publishAggregatedHistoryHandler;

    PipelineManager pipelineManager;
    PerformanceCalculator performanceCalculator;
    PerformanceCalculatorContext performanceCalculatorContext;
    SignalsFinder signalsFinder;
    SignalsFinderContext signalsFinderContext;
    SignalRegistry signalRegistry;
    SignalRegistryContext signalRegistryContext;
    RiskManagerSink riskManagerSink;

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
        intradayJournal = new FakeIntradayJournal();
        signalJournal = new FakeSignalJournal();
        datasourceRepository = new FakeDatasourceRepository();
        instrumentRepository = new FakeInstrumentRepository(datasourceRepository);
        scannerRepository = new FakeScannerRepository();
        telegramChatRepository = new FakeTelegramChatRepository();
        emulatedPositionJournal = new FakeEmulatedPositionRepository();

        publishAggregatedHistoryHandler = new PublishAggregatedHistoryHandler(
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
            emulatedPositionJournal
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
            intradayJournal,
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
                publishAggregatedHistoryHandler
            )
        );

        performanceCalculatorContext = new PerformanceCalculatorContext();
        signalsFinderContext = new SignalsFinderContext();
        signalRegistryContext = new SignalRegistryContext();
        performanceCalculator = new PerformanceCalculator(performanceCalculatorContext);
        signalsFinder = new SignalsFinder(signalsFinderContext);
        signalRegistry = new SignalRegistry(signalRegistryContext, signalJournal);
        riskManagerSink = new RiskManagerSink();

        pipelineManager = new PipelineManager(
            signalJournal,
            scannerRepository,
            aggregatedTotalsJournal,
            datasourceRepository,
            signalRegistryContext,
            performanceCalculatorContext,
            signalsFinderContext
        );
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setDatasourceStorage(datasourceStorage);
        return fakeExchangeProvider;
    }
}