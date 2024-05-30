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
import ru.ioque.investfund.application.modules.datasource.handler.RegisterDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.UnregisterDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.UpdateDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.SynchronizeDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.PublishIntradayDataHandler;
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.application.modules.pipeline.PipelineManager;
import ru.ioque.investfund.application.modules.pipeline.SignalRegistry;
import ru.ioque.investfund.application.modules.pipeline.processor.EmulatedPositionManager;
import ru.ioque.investfund.application.modules.pipeline.processor.EvaluateRiskProcessor;
import ru.ioque.investfund.application.modules.pipeline.processor.SignalProducer;
import ru.ioque.investfund.application.modules.risk.handler.CloseEmulatedPositionHandler;
import ru.ioque.investfund.application.modules.risk.handler.OpenEmulatedPositionHandler;
import ru.ioque.investfund.application.modules.scanner.handler.ActivateScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.CreateScannerCommandHandler;
import ru.ioque.investfund.application.modules.scanner.handler.DeactivateScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.RemoveScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.UpdateScannerCommandHandler;
import ru.ioque.investfund.application.modules.telegrambot.handler.PublishSignalHandler;
import ru.ioque.investfund.application.modules.telegrambot.handler.SubscribeHandler;
import ru.ioque.investfund.application.modules.telegrambot.handler.UnsubscribeHandler;
import ru.ioque.investfund.fakes.journal.FakeAggregatedTotalsJournal;
import ru.ioque.investfund.fakes.journal.FakeIntradayJournal;
import ru.ioque.investfund.fakes.journal.FakeSignalJournal;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FakeDIContainer {
    DatasourceStorage datasourceStorage;
    FakeDateTimeProvider dateTimeProvider;
    FakeDatasourceProvider exchangeProvider;
    FakeLoggerProvider loggerProvider;

    FakeSignalJournal signalJournal;
    FakeIntradayJournal intradayJournal;
    FakeAggregatedTotalsJournal aggregatedHistoryJournal;
    FakeEmulatedPositionJournal emulatedPositionJournal;

    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    FakeInstrumentRepository instrumentRepository;
    FakeTelegramChatRepository telegramChatRepository;

    FakeTelegramMessageSender telegramMessageSender;

    DisableUpdateInstrumentHandler disableUpdateInstrumentProcessor;
    EnableUpdateInstrumentHandler enableUpdateInstrumentProcessor;
    SynchronizeDatasourceHandler integrateInstrumentsProcessor;
    PublishIntradayDataHandler integrateTradingDataProcessor;
    RegisterDatasourceHandler registerDatasourceProcessor;
    UnregisterDatasourceHandler unregisterDatasourceProcessor;
    UpdateDatasourceHandler updateDatasourceProcessor;
    CreateScannerCommandHandler createScannerProcessor;
    UpdateScannerCommandHandler updateScannerProcessor;
    RemoveScannerHandler removeScannerHandler;
    CloseEmulatedPositionHandler closeEmulatedPositionHandler;
    OpenEmulatedPositionHandler openEmulatedPositionHandler;
    PublishSignalHandler publishSignalHandler;
    SubscribeHandler subscribeHandler;
    UnsubscribeHandler unsubscribeHandler;
    DeactivateScannerHandler deactivateScannerHandler;
    ActivateScannerHandler activateScannerHandler;
    PublishAggregatedHistoryHandler publishAggregatedHistoryHandler;

    PipelineContext pipelineContext;
    SignalRegistry signalRegistry;
    PipelineManager pipelineManager;
    SignalProducer signalProducer;
    EvaluateRiskProcessor evaluateRiskProcessor;
    EmulatedPositionManager emulatedPositionManager;

    CommandBus commandBus;
    Validator validator;

    public FakeDIContainer() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        aggregatedHistoryJournal = new FakeAggregatedTotalsJournal();
        intradayJournal = new FakeIntradayJournal();
        signalJournal = new FakeSignalJournal();

        dateTimeProvider = new FakeDateTimeProvider();
        datasourceStorage = new DatasourceStorage();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        datasourceRepository = new FakeDatasourceRepository();
        instrumentRepository = new FakeInstrumentRepository(datasourceRepository);
        scannerRepository = new FakeScannerRepository();
        telegramChatRepository = new FakeTelegramChatRepository();
        emulatedPositionJournal = new FakeEmulatedPositionJournal();
        telegramMessageSender = new FakeTelegramMessageSender();

        publishAggregatedHistoryHandler = new PublishAggregatedHistoryHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            datasourceRepository,
            aggregatedHistoryJournal
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
        disableUpdateInstrumentProcessor = new DisableUpdateInstrumentHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        enableUpdateInstrumentProcessor = new EnableUpdateInstrumentHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        integrateInstrumentsProcessor = new SynchronizeDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            instrumentRepository,
            datasourceRepository
        );
        integrateTradingDataProcessor = new PublishIntradayDataHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            intradayJournal,
            datasourceRepository
        );
        registerDatasourceProcessor = new RegisterDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        unregisterDatasourceProcessor = new UnregisterDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        updateDatasourceProcessor = new UpdateDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        createScannerProcessor = new CreateScannerCommandHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            datasourceRepository
        );
        updateScannerProcessor = new UpdateScannerCommandHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            datasourceRepository
        );
        closeEmulatedPositionHandler = new CloseEmulatedPositionHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            emulatedPositionJournal
        );
        openEmulatedPositionHandler = new OpenEmulatedPositionHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            emulatedPositionJournal,
            instrumentRepository,
            scannerRepository
        );
        publishSignalHandler = new PublishSignalHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            aggregatedHistoryJournal,
            scannerRepository,
            instrumentRepository,
            telegramChatRepository,
            telegramMessageSender
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
                disableUpdateInstrumentProcessor,
                enableUpdateInstrumentProcessor,
                integrateInstrumentsProcessor,
                integrateTradingDataProcessor,
                registerDatasourceProcessor,
                updateDatasourceProcessor,
                unregisterDatasourceProcessor,
                createScannerProcessor,
                updateScannerProcessor,
                closeEmulatedPositionHandler,
                openEmulatedPositionHandler,
                publishSignalHandler,
                subscribeHandler,
                unsubscribeHandler,
                removeScannerHandler,
                deactivateScannerHandler,
                activateScannerHandler,
                publishAggregatedHistoryHandler
            )
        );

        pipelineContext = new PipelineContext();
        signalRegistry = new SignalRegistry();
        pipelineManager = new PipelineManager(
            pipelineContext,
            signalRegistry,
            scannerRepository,
            instrumentRepository,
            aggregatedHistoryJournal,
            signalJournal
        );
        signalProducer = new SignalProducer(
            pipelineContext,
            signalRegistry,
            dateTimeProvider,
            signalJournal
        );
        evaluateRiskProcessor = new EvaluateRiskProcessor(pipelineContext);
        emulatedPositionManager = new EmulatedPositionManager(pipelineContext, emulatedPositionJournal);
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setDatasourceStorage(datasourceStorage);
        return fakeExchangeProvider;
    }
}