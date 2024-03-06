package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.schedule.ScheduleCommand;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalTime;

public class BaseScannerTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        scheduleManager().saveScheduleUnit(
            new ScheduleCommand(
                SystemModule.EXCHANGE,
                LocalTime.parse("00:00"),
                LocalTime.parse("23:59"),
                1
            )
        );
        scheduleManager().saveScheduleUnit(
            new ScheduleCommand(
                SystemModule.SIGNAL_SCANNER,
                LocalTime.parse("10:00"),
                LocalTime.parse("19:00"),
                2
            )
        );
        loggerProvider().clearLogs();
    }

    protected void runWorkPipline() {
        exchangeManager().integrateTradingData();
        statisticManager().calcStatistic();
        dataScannerManager().scanning();
    }
}
