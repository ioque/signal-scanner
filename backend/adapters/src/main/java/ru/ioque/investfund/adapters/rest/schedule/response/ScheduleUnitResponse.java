package ru.ioque.investfund.adapters.rest.schedule.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUnitResponse {
    String moduleName;
    SystemModule systemModuleCode;
    LocalTime startTime;
    LocalTime stopTime;
    Integer priority;

    public static ScheduleUnitResponse fromDomain(ScheduleUnit scheduleUnit) {
        return ScheduleUnitResponse.builder()
            .moduleName(scheduleUnit.getSystemModule().getModuleName())
            .systemModuleCode(scheduleUnit.getSystemModule())
            .startTime(scheduleUnit.getStartTime())
            .stopTime(scheduleUnit.getStopTime())
            .priority(scheduleUnit.getPriority())
            .build();
    }
}
