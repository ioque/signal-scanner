package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.handler.UpdateAggregateHistoryHandler;
import ru.ioque.investfund.application.modules.datasource.handler.DisableUpdateInstrumentHandler;
import ru.ioque.investfund.application.modules.datasource.handler.EnableUpdateInstrumentHandler;
import ru.ioque.investfund.application.modules.datasource.handler.RegisterDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.UnregisterDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.UpdateDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.SynchronizeDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.PublishIntradayDataHandler;
import ru.ioque.investfund.application.modules.risk.handler.CloseEmulatedPositionHandler;
import ru.ioque.investfund.application.modules.risk.handler.EvaluateEmulatedPositionHandler;
import ru.ioque.investfund.application.modules.risk.handler.OpenEmulatedPositionHandler;
import ru.ioque.investfund.application.modules.scanner.handler.ActivateScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.CreateScannerCommandHandler;
import ru.ioque.investfund.application.modules.scanner.handler.DeactivateScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.ProduceSignalCommandHandler;
import ru.ioque.investfund.application.modules.scanner.handler.RemoveScannerHandler;
import ru.ioque.investfund.application.modules.scanner.handler.UpdateScannerCommandHandler;
import ru.ioque.investfund.application.modules.telegrambot.PublishSignalHandler;
import ru.ioque.investfund.application.modules.telegrambot.SubscribeHandler;
import ru.ioque.investfund.application.modules.telegrambot.UnsubscribeHandler;
import ru.ioque.investfund.fixture.DatasourceStorage;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FakeDIContainer {
    DatasourceStorage datasourceStorage;
    FakeDateTimeProvider dateTimeProvider;
    FakeDatasourceProvider exchangeProvider;
    FakeLoggerProvider loggerProvider;
    FakeEventJournal eventPublisher;
    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    FakeInstrumentRepository instrumentRepository;
    FakeIntradayValueRepository intradayValueRepository;
    FakeTelegramChatRepository telegramChatRepository;
    FakeEmulatedPositionRepository emulatedPositionRepository;
    FakeTelegramMessageSender telegramMessageSender;
    FakeIntradayJournal intradayJournalPublisher;
    DisableUpdateInstrumentHandler disableUpdateInstrumentProcessor;
    EnableUpdateInstrumentHandler enableUpdateInstrumentProcessor;
    SynchronizeDatasourceHandler integrateInstrumentsProcessor;
    PublishIntradayDataHandler integrateTradingDataProcessor;
    RegisterDatasourceHandler registerDatasourceProcessor;
    UnregisterDatasourceHandler unregisterDatasourceProcessor;
    UpdateDatasourceHandler updateDatasourceProcessor;
    CreateScannerCommandHandler createScannerProcessor;
    ProduceSignalCommandHandler produceSignalProcessor;
    UpdateScannerCommandHandler updateScannerProcessor;
    CloseEmulatedPositionHandler closeEmulatedPositionHandler;
    EvaluateEmulatedPositionHandler evaluateEmulatedPositionHandler;
    OpenEmulatedPositionHandler openEmulatedPositionHandler;
    RemoveScannerHandler removeScannerHandler;
    PublishSignalHandler publishSignalHandler;
    SubscribeHandler subscribeHandler;
    UnsubscribeHandler unsubscribeHandler;
    DeactivateScannerHandler deactivateScannerHandler;
    ActivateScannerHandler activateScannerHandler;
    UpdateAggregateHistoryHandler updateAggregateHistoryHandler;
    CommandBus commandBus;
    Validator validator;

    public FakeDIContainer() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        eventPublisher = new FakeEventJournal();
        dateTimeProvider = new FakeDateTimeProvider();
        datasourceStorage = new DatasourceStorage();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        datasourceRepository = new FakeDatasourceRepository();
        instrumentRepository = new FakeInstrumentRepository(datasourceRepository);
        intradayValueRepository = new FakeIntradayValueRepository();
        scannerRepository = new FakeScannerRepository();
        telegramChatRepository = new FakeTelegramChatRepository();
        emulatedPositionRepository = new FakeEmulatedPositionRepository();
        telegramMessageSender = new FakeTelegramMessageSender();
        intradayJournalPublisher = new FakeIntradayJournal();

        updateAggregateHistoryHandler = new UpdateAggregateHistoryHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            datasourceRepository
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
          emulatedPositionRepository
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
            intradayJournalPublisher,
            datasourceRepository,
            intradayValueRepository,
            eventPublisher
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
        produceSignalProcessor = new ProduceSignalCommandHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            instrumentRepository,
            eventPublisher
        );
        closeEmulatedPositionHandler = new CloseEmulatedPositionHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            emulatedPositionRepository
        );
        evaluateEmulatedPositionHandler = new EvaluateEmulatedPositionHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            emulatedPositionRepository
        );
        openEmulatedPositionHandler = new OpenEmulatedPositionHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            emulatedPositionRepository,
            instrumentRepository,
            scannerRepository
        );
        publishSignalHandler = new PublishSignalHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
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
                produceSignalProcessor,
                updateScannerProcessor,
                closeEmulatedPositionHandler,
                evaluateEmulatedPositionHandler,
                openEmulatedPositionHandler,
                publishSignalHandler,
                subscribeHandler,
                unsubscribeHandler,
                removeScannerHandler,
                deactivateScannerHandler,
                activateScannerHandler,
                updateAggregateHistoryHandler
            )
        );
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setDatasourceStorage(datasourceStorage);
        return fakeExchangeProvider;
    }
}