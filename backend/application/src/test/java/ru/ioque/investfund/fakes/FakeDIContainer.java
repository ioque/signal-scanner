package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.datasource.DatasourceManager;
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
    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository exchangeRepository;
    LoggerFacade loggerFacade;
    ScannerManager scannerManager;
    DatasourceManager datasourceManager;
    FakeEventBus eventBus;

    public FakeDIContainer() {
        eventBus = new FakeEventBus();
        dateTimeProvider = new FakeDateTimeProvider();
        exchangeDataFixture = new ExchangeDataFixture();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        uuidProvider = new FakeUUIDProvider();
        scannerLogRepository = new FakeScannerLogRepository();
        loggerFacade = new LoggerFacade(loggerProvider);
        exchangeRepository = new FakeDatasourceRepository();
        finInstrumentRepository = new FakeFinInstrumentRepository(exchangeRepository, dateTimeProvider);
        scannerRepository = new FakeScannerRepository(finInstrumentRepository);
        datasourceManager = new DatasourceManager(
            dateTimeProvider,
            exchangeProvider,
            exchangeRepository,
            uuidProvider,
            loggerFacade,
            eventBus
        );
        scannerManager = new ScannerManager(
            scannerRepository,
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