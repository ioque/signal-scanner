package ru.ioque.investfund.application.modules.schedule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScheduleRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.schedule.Schedule;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScheduleManager {
    ScheduleTaskExecutor scheduleExecutor;
    ScheduleRepository scheduleRepository;
    DateTimeProvider dateTimeProvider;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;

    public synchronized Schedule getCurrentSchedule() {
        return scheduleRepository.getSchedule();
    }

    public synchronized void executeSchedule() {
        final var currentDateTime = dateTimeProvider.nowDateTime();
        final var schedule = scheduleRepository.getSchedule();
        if (schedule.getActualScheduleUnits(currentDateTime).toList().isEmpty()) {
            loggerFacade.logNotActualScheduleUnit(currentDateTime);
            return;
        }
        loggerFacade.logRunExecuteSchedule(currentDateTime);
        scheduleRepository.save(scheduleExecutor.execute(schedule, currentDateTime));
        loggerFacade.logFinishExecuteSchedule(currentDateTime, dateTimeProvider.nowDateTime());
    }

    public synchronized void saveScheduleUnit(ScheduleCommand command) {
        final Schedule schedule = scheduleRepository.getSchedule();
        final ScheduleUnit scheduleUnit = ScheduleUnit.builder()
            .id(schedule.getScheduleUnitBy(command.getSystemModule()).map(ScheduleUnit::getId).orElse(uuidProvider.generate()))
            .systemModule(command.getSystemModule())
            .startTime(command.getStartTime())
            .stopTime(command.getStopTime())
            .priority(command.getPriority())
            .build();
        schedule.saveScheduleUnit(scheduleUnit);
        scheduleRepository.save(schedule);
        loggerFacade.logAddNewScheduleUnit(scheduleUnit);
    }
    public void clearSchedule() {
        scheduleRepository.delete();
    }
}
