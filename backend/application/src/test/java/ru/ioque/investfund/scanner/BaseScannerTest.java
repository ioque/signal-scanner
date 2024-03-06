package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.exchange.entity.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.statistic.StatisticCalculatedEvent;

public class BaseScannerTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        eventBus().subscribe(TradingDataUpdatedEvent.class, statisticManager());
        eventBus().subscribe(StatisticCalculatedEvent.class, dataScannerManager());
        loggerProvider().clearLogs();
    }

    protected void runWorkPipline() {
        exchangeManager().integrateTradingData();
    }
}
