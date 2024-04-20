package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.CommandBus;
import ru.ioque.investfund.application.datasource.DisableUpdateInstrumentHandler;
import ru.ioque.investfund.application.datasource.EnableUpdateInstrumentHandler;
import ru.ioque.investfund.application.datasource.IntegrateInstrumentsHandler;
import ru.ioque.investfund.application.datasource.IntegrateTradingDataHandler;
import ru.ioque.investfund.application.datasource.RegisterDatasourceHandler;
import ru.ioque.investfund.application.datasource.UnregisterDatasourceHandler;
import ru.ioque.investfund.application.datasource.UpdateDatasourceHandler;
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
    CreateScannerCommandHandler createScannerProcessor;
    ProduceSignalCommandHandler produceSignalProcessor;
    UpdateScannerCommandHandler updateScannerProcessor;
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
        tradingDataRepository = new FakeTradingSnapshotsRepository(datasourceRepository, intradayValueRepository, historyValueRepository, dateTimeProvider);
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
            uuidProvider,
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