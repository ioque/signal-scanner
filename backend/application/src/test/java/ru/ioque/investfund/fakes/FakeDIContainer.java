package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.fixture.ExchangeDataFixture;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FakeDIContainer {
    ExchangeDataFixture exchangeDataFixture;
    FakeDateTimeProvider dateTimeProvider;
    FakeExchangeProvider exchangeProvider;
    FakeLoggerProvider loggerProvider;
    FakeUUIDProvider uuidProvider;
    FakeScannerLogRepository scannerLogRepository;
    FakeFinInstrumentRepository finInstrumentRepository;
    FakeConfigureProvider configureProvider;
    FakeScannerRepository scannerRepository;
    FakeExchangeRepository exchangeRepository;
    LoggerFacade loggerFacade;
    ScannerManager scannerManager;
    ExchangeManager exchangeManager;
    FakeEventBus eventBus;

    public FakeDIContainer() {
        eventBus = new FakeEventBus();
        dateTimeProvider = new FakeDateTimeProvider();
        exchangeDataFixture = new ExchangeDataFixture();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        uuidProvider = new FakeUUIDProvider();
        scannerLogRepository = new FakeScannerLogRepository();
        configureProvider = new FakeConfigureProvider();
        loggerFacade = new LoggerFacade(loggerProvider);
        exchangeRepository = new FakeExchangeRepository();
        finInstrumentRepository = new FakeFinInstrumentRepository(exchangeRepository);
        scannerRepository = new FakeScannerRepository(finInstrumentRepository);
        exchangeManager = new ExchangeManager(
            dateTimeProvider,
            configureProvider,
            exchangeProvider,
            exchangeRepository,
            uuidProvider,
            loggerFacade,
            eventBus
        );
        scannerManager = new ScannerManager(
            finInstrumentRepository,
            scannerRepository,
            scannerLogRepository,
            uuidProvider,
            dateTimeProvider,
            loggerFacade
        );
    }

    private FakeExchangeProvider getFakeExchangeProvider() {
        FakeExchangeProvider fakeExchangeProvider = new FakeExchangeProvider(dateTimeProvider);
        fakeExchangeProvider.setExchangeDataFixture(exchangeDataFixture);
        return fakeExchangeProvider;
    }
}