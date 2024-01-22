package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.ConfigureProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ReportRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.ScheduleRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.exchange.ExchangeCache;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.application.modules.schedule.ScheduleManager;
import ru.ioque.investfund.application.modules.schedule.ScheduleTaskExecutor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.fixture.ExchangeDataFixture;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FakeDIContainer {
    ExchangeDataFixture exchangeDataFixture;
    DateTimeProvider dateTimeProvider;
    ExchangeProvider exchangeProvider;
    LoggerProvider loggerProvider;
    UUIDProvider uuidProvider;
    ReportRepository reportRepository;
    ConfigureProvider configureProvider;
    ScheduleRepository scheduleRepository;
    ScannerRepository scannerRepository;
    ExchangeRepository exchangeRepository;
    LoggerFacade loggerFacade;
    ScannerManager scannerManager;
    ScheduleTaskExecutor scheduleTaskExecutor;
    ScheduleManager scheduleManager;
    ExchangeManager exchangeManager;

    public FakeDIContainer() {
        dateTimeProvider = new FakeDateTimeProvider();
        scheduleRepository = new FakeScheduleRepository();
        exchangeDataFixture = new ExchangeDataFixture();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        uuidProvider = new FakeUUIDProvider();
        reportRepository = new FakeReportRepository();
        configureProvider = new FakeConfigureProvider();
        loggerFacade = new LoggerFacade(loggerProvider);
        exchangeRepository = new FakeExchangeRepository();
        scannerRepository = new FakeScannerRepository(exchangeRepository);
        exchangeManager = new ExchangeManager(
            dateTimeProvider,
            configureProvider,
            exchangeProvider,
            exchangeRepository,
            uuidProvider,
            loggerFacade,
            new ExchangeCache()
        );
        scannerManager = new ScannerManager(
            scannerRepository,
            reportRepository,
            uuidProvider,
            dateTimeProvider,
            loggerFacade
        );
        scheduleTaskExecutor = new ScheduleTaskExecutor(
            dateTimeProvider,
            exchangeManager,
            scannerManager
        );
        scheduleManager = new ScheduleManager(
            scheduleTaskExecutor,
            scheduleRepository,
            dateTimeProvider,
            uuidProvider,
            loggerFacade
        );
    }

    private FakeExchangeProvider getFakeExchangeProvider() {
        FakeExchangeProvider fakeExchangeProvider = new FakeExchangeProvider(dateTimeProvider);
        fakeExchangeProvider.setExchangeDataFixture(exchangeDataFixture);
        return fakeExchangeProvider;
    }
}
