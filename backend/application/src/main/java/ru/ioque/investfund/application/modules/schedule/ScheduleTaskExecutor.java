package ru.ioque.investfund.application.modules.schedule;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.domain.schedule.Schedule;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScheduleTaskExecutor {
    DateTimeProvider dateTimeProvider;
    ExchangeManager exchangeManager;
    ScannerManager scannerManager;


    //перейти к полиморфному эндпоинту по примеру сканеров
    Map<SystemModule, Consumer<ScheduleUnit>> handlers = Map.of(
        SystemModule.EXCHANGE, this::executeAggregatorScheduleUnit,
        SystemModule.SIGNAL_SCANNER, this::signalScannerSchedule
    );

    public Schedule execute(Schedule schedule, LocalDateTime today) {
        schedule.getActualScheduleUnits(today).forEach(this::handle);
        return schedule;
    }

    private void handle(ScheduleUnit scheduleUnit) {
        handlers.get(scheduleUnit.getSystemModule()).accept(scheduleUnit);
    }

    private void signalScannerSchedule(ScheduleUnit scheduleUnit) {
        scannerManager.execute();
    }

    private void executeAggregatorScheduleUnit(ScheduleUnit scheduleUnit) {
        if (scheduleUnit.isTimeToExecuteBy(dateTimeProvider.nowDateTime())) {
            exchangeManager.integrateTradingData();
            scheduleUnit.setLastExecutionLocalDateTime(dateTimeProvider.nowDateTime());
        }
    }
}
