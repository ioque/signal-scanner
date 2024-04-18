package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.command.CommandBus;
import ru.ioque.investfund.application.command.handlers.datasource.DisableUpdateInstrumentHandler;
import ru.ioque.investfund.application.command.handlers.datasource.EnableUpdateInstrumentHandler;
import ru.ioque.investfund.application.command.handlers.datasource.IntegrateInstrumentsHandler;
import ru.ioque.investfund.application.command.handlers.datasource.IntegrateTradingDataHandler;
import ru.ioque.investfund.application.command.handlers.datasource.RegisterDatasourceHandler;
import ru.ioque.investfund.application.command.handlers.datasource.UnregisterDatasourceHandler;
import ru.ioque.investfund.application.command.handlers.datasource.UpdateDatasourceHandler;
import ru.ioque.investfund.application.command.handlers.scanner.CreateScannerHandler;
import ru.ioque.investfund.application.command.handlers.scanner.ProduceSignalHandler;
import ru.ioque.investfund.application.command.handlers.scanner.UpdateScannerHandler;
import ru.ioque.investfund.application.command.handlers.telegrambot.TelegramBotService;
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
    FakeHistoryValueRepository historyValueRepository;
    TelegramBotService telegramBotService;
    FakeTelegramChatRepository telegramChatRepository;
    FakeTelegramMessageSender telegramMessageSender;
    DisableUpdateInstrumentHandler disableUpdateInstrumentProcessor;
    EnableUpdateInstrumentHandler enableUpdateInstrumentProcessor;
    IntegrateInstrumentsHandler integrateInstrumentsProcessor;
    IntegrateTradingDataHandler integrateTradingDataProcessor;
    RegisterDatasourceHandler registerDatasourceProcessor;
    UnregisterDatasourceHandler unregisterDatasourceProcessor;
    UpdateDatasourceHandler updateDatasourceProcessor;
    CreateScannerHandler createScannerProcessor;
    ProduceSignalHandler produceSignalProcessor;
    UpdateScannerHandler updateScannerProcessor;
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
        historyValueRepository = new FakeHistoryValueRepository();
        scannerRepository = new FakeScannerRepository();
        tradingDataRepository = new FakeTradingSnapshotsRepository(intradayValueRepository, historyValueRepository, dateTimeProvider);
        telegramChatRepository = new FakeTelegramChatRepository();
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
            exchangeProvider,
            datasourceRepository
        );
        integrateTradingDataProcessor = new IntegrateTradingDataHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            datasourceRepository,
            historyValueRepository,
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
        createScannerProcessor = new CreateScannerHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            scannerRepository,
            datasourceRepository
        );
        updateScannerProcessor = new UpdateScannerHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            datasourceRepository
        );
        produceSignalProcessor = new ProduceSignalHandler(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            scannerRepository,
            tradingDataRepository,
            eventPublisher
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
                updateScannerProcessor
            )
        );
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setDatasourceStorage(datasourceStorage);
        return fakeExchangeProvider;
    }
}