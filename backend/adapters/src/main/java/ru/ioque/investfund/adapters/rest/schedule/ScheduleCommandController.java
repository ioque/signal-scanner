package ru.ioque.investfund.adapters.rest.schedule;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.schedule.request.SaveScheduleRequest;
import ru.ioque.investfund.application.modules.schedule.ScheduleCommand;
import ru.ioque.investfund.application.modules.schedule.ScheduleManager;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ScheduleCommandController", description="Контроллер команд к модулю \"SCHEDULE\"")
public class ScheduleCommandController {
    ScheduleManager scheduleManager;

    @PostMapping("/api/v1/schedule")
    public void saveSchedule(@RequestBody SaveScheduleRequest scheduleRequest) {
        scheduleRequest
            .getSaveScheduleUnitRequests()
            .forEach(scheduleUnit ->
                scheduleManager
                    .saveScheduleUnit(
                        new ScheduleCommand(
                            scheduleUnit.getSystemModuleCode(),
                            scheduleUnit.getStartTime(),
                            scheduleUnit.getStopTime(),
                            scheduleUnit.getPriority()
                        )
                    )
            );
    }

    @DeleteMapping("/api/v1/schedule")
    public void clearSchedule() {
        scheduleManager.clearSchedule();
    }
}
