package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.ConfigureProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.exchange.ExchangeCache;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.application.modules.statistic.StatisticManager;
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
    ScannerLogRepository scannerLogRepository;
    ConfigureProvider configureProvider;
    ScannerRepository scannerRepository;
    ExchangeRepository exchangeRepository;
    LoggerFacade loggerFacade;
    ScannerManager scannerManager;
    ExchangeManager exchangeManager;
    EventBus eventBus;
    StatisticRepository statisticRepository;
    StatisticManager statisticManager;

    public FakeDIContainer() {
        statisticRepository = new FakeStatisticRepository();
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
        scannerRepository = new FakeScannerRepository(exchangeRepository);
        exchangeManager = new ExchangeManager(
            dateTimeProvider,
            configureProvider,
            exchangeProvider,
            exchangeRepository,
            uuidProvider,
            loggerFacade,
            new ExchangeCache(),
            eventBus
        );
        scannerManager = new ScannerManager(
            scannerRepository,
            scannerLogRepository,
            statisticRepository,
            uuidProvider,
            dateTimeProvider,
            loggerFacade
        );
        statisticManager = new StatisticManager(statisticRepository, exchangeRepository, eventBus, uuidProvider, dateTimeProvider);
    }

    private FakeExchangeProvider getFakeExchangeProvider() {
        FakeExchangeProvider fakeExchangeProvider = new FakeExchangeProvider(dateTimeProvider);
        fakeExchangeProvider.setExchangeDataFixture(exchangeDataFixture);
        return fakeExchangeProvider;
    }
}
