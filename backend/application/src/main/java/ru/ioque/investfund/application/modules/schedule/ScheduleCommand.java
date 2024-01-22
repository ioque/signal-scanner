package ru.ioque.investfund.application.modules.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ScheduleCommand {
    SystemModule systemModule;
    LocalTime startTime;
    LocalTime stopTime;
    Integer priority;
}
