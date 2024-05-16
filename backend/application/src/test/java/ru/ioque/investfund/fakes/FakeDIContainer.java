package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.handler.configurator.DisableUpdateInstrumentHandler;
import ru.ioque.investfund.application.modules.datasource.handler.configurator.EnableUpdateInstrumentHandler;
import ru.ioque.investfund.application.modules.datasource.handler.configurator.RegisterDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.configurator.UnregisterDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.configurator.UpdateDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.integration.PrepareForWorkDatasourceHandler;
import ru.ioque.investfund.application.modules.datasource.handler.integration.IntegrateTradingDataHandler;
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
    FakeEventPublisher eventPublisher;
    FakeTradingSnapshotsRepository tradingDataRepository;
    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    FakeInstrumentRepository instrumentRepository;
    FakeIntradayValueRepository intradayValueRepository;
    FakeTelegramChatRepository telegramChatRepository;
    FakeEmulatedPositionRepository emulatedPositionRepository;
    FakeTelegramMessageSender telegramMessageSender;
    DisableUpdateInstrumentHandler disableUpdateInstrumentProcessor;
    EnableUpdateInstrumentHandler enableUpdateInstrumentProcessor;
    PrepareForWorkDatasourceHandler integrateInstrumentsProcessor;
    IntegrateTradingDataHandler integrateTradingDataProcessor;
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
    CommandBus commandBus;
    Validator validator;

    public FakeDIContainer() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        eventPublisher = new FakeEventPublisher();
        dateTimeProvider = new FakeDateTimeProvider();
        datasourceStorage = new DatasourceStorage();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        datasourceRepository = new FakeDatasourceRepository();
        instrumentRepository = new FakeInstrumentRepository(datasourceRepository);
        intradayValueRepository = new FakeIntradayValueRepository();
        scannerRepository = new FakeScannerRepository();
        tradingDataRepository = new FakeTradingSnapshotsRepository(datasourceRepository, dateTimeProvider);
        telegramChatRepository = new FakeTelegramChatRepository();
        emulatedPositionRepository = new FakeEmulatedPositionRepository();
        telegramMessageSender = new FakeTelegramMessageSender();
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
        integrateInstrumentsProcessor = new PrepareForWorkDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            instrumentRepository,
            datasourceRepository
        );
        integrateTradingDataProcessor = new IntegrateTradingDataHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
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
            tradingDataRepository,
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
            tradingDataRepository,
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
                activateScannerHandler
            )
        );
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setDatasourceStorage(datasourceStorage);
        return fakeExchangeProvider;
    }
}