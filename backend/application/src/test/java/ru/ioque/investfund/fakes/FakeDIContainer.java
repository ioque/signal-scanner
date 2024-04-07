package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.configurator.ScannerConfigurator;
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
    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    LoggerFacade loggerFacade;
    ScannerConfigurator scannerConfigurator;
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
        datasourceRepository = new FakeDatasourceRepository();
        scannerRepository = new FakeScannerRepository(datasourceRepository, dateTimeProvider);
        datasourceManager = new DatasourceManager(
            dateTimeProvider,
            exchangeProvider,
            datasourceRepository,
            uuidProvider,
            loggerFacade,
            eventBus
        );
        scannerConfigurator = new ScannerConfigurator(
            scannerRepository, uuidProvider, loggerFacade
        );
        scannerManager = new ScannerManager(
            scannerRepository,
            scannerLogRepository,
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