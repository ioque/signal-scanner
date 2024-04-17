package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.CommandBus;
import ru.ioque.investfund.application.modules.datasource.DisableUpdateInstrumentProcessor;
import ru.ioque.investfund.application.modules.datasource.EnableUpdateInstrumentProcessor;
import ru.ioque.investfund.application.modules.datasource.IntegrateInstrumentsProcessor;
import ru.ioque.investfund.application.modules.datasource.IntegrateTradingDataProcessor;
import ru.ioque.investfund.application.modules.datasource.RegisterDatasourceProcessor;
import ru.ioque.investfund.application.modules.datasource.UnregisterDatasourceProcessor;
import ru.ioque.investfund.application.modules.datasource.UpdateDatasourceProcessor;
import ru.ioque.investfund.application.modules.scanner.CreateScannerProcessor;
import ru.ioque.investfund.application.modules.scanner.ProduceSignalProcessor;
import ru.ioque.investfund.application.modules.scanner.UpdateScannerProcessor;
import ru.ioque.investfund.application.modules.telegrambot.TelegramBotService;
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
    DisableUpdateInstrumentProcessor disableUpdateInstrumentProcessor;
    EnableUpdateInstrumentProcessor enableUpdateInstrumentProcessor;
    IntegrateInstrumentsProcessor integrateInstrumentsProcessor;
    IntegrateTradingDataProcessor integrateTradingDataProcessor;
    RegisterDatasourceProcessor registerDatasourceProcessor;
    UnregisterDatasourceProcessor unregisterDatasourceProcessor;
    UpdateDatasourceProcessor updateDatasourceProcessor;
    CreateScannerProcessor createScannerProcessor;
    ProduceSignalProcessor produceSignalProcessor;
    UpdateScannerProcessor  updateScannerProcessor;
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
        disableUpdateInstrumentProcessor = new DisableUpdateInstrumentProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        enableUpdateInstrumentProcessor = new EnableUpdateInstrumentProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        integrateInstrumentsProcessor = new IntegrateInstrumentsProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            datasourceRepository
        );
        integrateTradingDataProcessor = new IntegrateTradingDataProcessor(
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
        registerDatasourceProcessor = new RegisterDatasourceProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            datasourceRepository
        );
        unregisterDatasourceProcessor = new UnregisterDatasourceProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        updateDatasourceProcessor = new UpdateDatasourceProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        createScannerProcessor = new CreateScannerProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            scannerRepository,
            datasourceRepository
        );
        updateScannerProcessor = new UpdateScannerProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            datasourceRepository
        );
        produceSignalProcessor = new ProduceSignalProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
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