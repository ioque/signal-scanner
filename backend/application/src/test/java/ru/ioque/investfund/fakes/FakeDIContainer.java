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
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.application.modules.pipeline.PipelineConfigurator;
import ru.ioque.investfund.application.modules.pipeline.SignalRegistry;
import ru.ioque.investfund.application.modules.pipeline.processor.PositionManager;
import ru.ioque.investfund.application.modules.pipeline.processor.RiskManager;
import ru.ioque.investfund.application.modules.pipeline.processor.SignalProducer;
import ru.ioque.investfund.application.modules.pipeline.processor.TelegramPublisher;
import ru.ioque.investfund.application.modules.scanner.handler.ActivateScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.CreateScannerCommandHandler;
import ru.ioque.investfund.application.modules.scanner.handler.DeactivateScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.RemoveScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.UpdateScannerCommandHandler;
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
    FakeAggregatedTotalsJournal aggregatedTotalsJournal;
    FakeEmulatedPositionJournal emulatedPositionJournal;

    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    FakeInstrumentRepository instrumentRepository;
    FakeTelegramChatRepository telegramChatRepository;

    FakeTelegramMessageSender telegramMessageSender;

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

    PipelineContext pipelineContext;
    SignalRegistry signalRegistry;
    PipelineConfigurator pipelineConfigurator;
    SignalProducer signalProducer;
    RiskManager riskManager;
    PositionManager positionManager;
    TelegramPublisher telegramPublisher;

    CommandBus commandBus;
    Validator validator;

    public FakeDIContainer() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        aggregatedTotalsJournal = new FakeAggregatedTotalsJournal();
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

        pipelineContext = new PipelineContext();
        signalRegistry = new SignalRegistry();
        pipelineConfigurator = new PipelineConfigurator(
            pipelineContext,
            signalRegistry,
            scannerRepository,
            instrumentRepository,
            aggregatedTotalsJournal,
            signalJournal
        );
        signalProducer = new SignalProducer(
            pipelineContext,
            signalRegistry,
            dateTimeProvider,
            signalJournal
        );
        riskManager = new RiskManager(pipelineContext);
        positionManager = new PositionManager(pipelineContext, emulatedPositionJournal);
        telegramPublisher = new TelegramPublisher(pipelineContext, telegramMessageSender, telegramChatRepository);
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setDatasourceStorage(datasourceStorage);
        return fakeExchangeProvider;
    }
}