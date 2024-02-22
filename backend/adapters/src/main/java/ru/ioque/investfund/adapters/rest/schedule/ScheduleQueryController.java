package ru.ioque.investfund.adapters.rest.schedule;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.schedule.response.ScheduleResponse;
import ru.ioque.investfund.application.modules.schedule.ScheduleManager;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ScheduleQueryController", description="Контроллер запросов к модулю \"SCHEDULE\"")
public class ScheduleQueryController {
    ScheduleManager scheduleManager;
    @GetMapping("/api/v1/schedule")
    public ScheduleResponse getSchedule() {
        return ScheduleResponse.fromDomain(scheduleManager.getCurrentSchedule());
    }

    @GetMapping("/api/v1/system-modules")
    public List<SystemModule> getSystemModules() {
        return Arrays.asList(SystemModule.values());
    }
}
