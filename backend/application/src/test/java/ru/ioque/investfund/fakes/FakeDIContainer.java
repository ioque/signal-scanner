package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
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
    FakeDatasourceProvider exchangeProvider;
    FakeLoggerProvider loggerProvider;
    FakeUUIDProvider uuidProvider;
    FakeTradingDataRepository tradingDataRepository;
    FakeScannerLogRepository scannerLogRepository;
    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    LoggerFacade loggerFacade;
    ScannerManager scannerManager;
    DatasourceManager datasourceManager;
    FakeEventPublisher eventPublisher;

    public FakeDIContainer() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        eventPublisher = new FakeEventPublisher();
        dateTimeProvider = new FakeDateTimeProvider();
        exchangeDataFixture = new ExchangeDataFixture();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        uuidProvider = new FakeUUIDProvider();
        scannerLogRepository = new FakeScannerLogRepository();
        loggerFacade = new LoggerFacade(loggerProvider);
        datasourceRepository = new FakeDatasourceRepository();
        scannerRepository = new FakeScannerRepository();
        tradingDataRepository = new FakeTradingDataRepository(datasourceRepository, dateTimeProvider);
        datasourceManager = new DatasourceManager(
            dateTimeProvider,
            exchangeProvider,
            datasourceRepository,
            uuidProvider,
            loggerFacade,
            eventPublisher
        );
        scannerManager = new ScannerManager(
            validator,
            datasourceRepository,
            tradingDataRepository,
            scannerRepository,
            scannerLogRepository,
            dateTimeProvider,
            uuidProvider,
            loggerFacade
        );
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setExchangeDataFixture(exchangeDataFixture);
        return fakeExchangeProvider;
    }
}