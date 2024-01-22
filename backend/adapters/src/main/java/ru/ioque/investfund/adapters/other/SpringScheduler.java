package ru.ioque.investfund.adapters.other;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.modules.schedule.ScheduleManager;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SpringScheduler {
    ScheduleManager scheduleManager;
    @Async
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void scheduleManagerRunTasks() {
        scheduleManager.executeSchedule();
    }
}
