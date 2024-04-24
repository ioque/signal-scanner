package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.api.command.CommandBus;
import ru.ioque.investfund.application.datasource.configurator.DisableUpdateInstrumentHandler;
import ru.ioque.investfund.application.datasource.configurator.EnableUpdateInstrumentHandler;
import ru.ioque.investfund.application.datasource.integration.IntegrateInstrumentsHandler;
import ru.ioque.investfund.application.datasource.integration.IntegrateTradingDataHandler;
import ru.ioque.investfund.application.datasource.configurator.RegisterDatasourceHandler;
import ru.ioque.investfund.application.datasource.configurator.UnregisterDatasourceHandler;
import ru.ioque.investfund.application.datasource.configurator.UpdateDatasourceHandler;
import ru.ioque.investfund.application.risk.CloseEmulatedPositionHandler;
import ru.ioque.investfund.application.risk.EvaluateEmulatedPositionHandler;
import ru.ioque.investfund.application.risk.OpenEmulatedPositionHandler;
import ru.ioque.investfund.application.scanner.CreateScannerCommandHandler;
import ru.ioque.investfund.application.scanner.ProduceSignalCommandHandler;
import ru.ioque.investfund.application.scanner.UpdateScannerCommandHandler;
import ru.ioque.investfund.application.telegrambot.TelegramBotService;
import ru.ioque.investfund.fixture.DatasourceStorage;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FakeDIContainer {
    DatasourceStorage datasourceStorage;
    FakeDateTimeProvider dateTimeProvider;
    FakeDatasourceProvider exchangeProvider;
    FakeLoggerProvider loggerProvider;
    FakeUUIDProvider uuidProvider;
    FakeEventPublisher eventPublisher;
    FakeTradingSnapshotsRepository tradingDataRepository;
    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    FakeIntradayValueRepository intradayValueRepository;
    TelegramBotService telegramBotService;
    FakeTelegramChatRepository telegramChatRepository;
    FakeEmulatedPositionRepository emulatedPositionRepository;
    FakeTelegramMessageSender telegramMessageSender;
    DisableUpdateInstrumentHandler disableUpdateInstrumentProcessor;
    EnableUpdateInstrumentHandler enableUpdateInstrumentProcessor;
    IntegrateInstrumentsHandler integrateInstrumentsProcessor;
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
        uuidProvider = new FakeUUIDProvider();
        datasourceRepository = new FakeDatasourceRepository();
        intradayValueRepository = new FakeIntradayValueRepository();
        scannerRepository = new FakeScannerRepository();
        tradingDataRepository = new FakeTradingSnapshotsRepository(datasourceRepository, dateTimeProvider);
        telegramChatRepository = new FakeTelegramChatRepository();
        emulatedPositionRepository = new FakeEmulatedPositionRepository();
        telegramMessageSender = new FakeTelegramMessageSender();
        telegramBotService = new TelegramBotService(dateTimeProvider, telegramChatRepository, telegramMessageSender);
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
        integrateInstrumentsProcessor = new IntegrateInstrumentsHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            exchangeProvider,
            datasourceRepository
        );
        integrateTradingDataProcessor = new IntegrateTradingDataHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            exchangeProvider,
            datasourceRepository,
            intradayValueRepository,
            eventPublisher
        );
        registerDatasourceProcessor = new RegisterDatasourceHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
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
            uuidProvider,
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
            uuidProvider,
            scannerRepository,
            tradingDataRepository,
            eventPublisher
        );
        closeEmulatedPositionHandler = new CloseEmulatedPositionHandler(
            dateTimeProvider,
            validator,
            loggerProvider
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
            loggerProvider
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
                openEmulatedPositionHandler
            )
        );
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setDatasourceStorage(datasourceStorage);
        return fakeExchangeProvider;
    }
}