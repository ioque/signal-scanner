package ru.ioque.investfund.domain.schedule;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.ioque.investfund.domain.Domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@ToString(callSuper = true)
@Getter(AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScheduleUnit extends Domain implements Comparable<ScheduleUnit> {
    SystemModule systemModule;
    LocalTime startTime;
    LocalTime stopTime;
    Integer priority;
    @NonFinal
    LocalDateTime lastExecutionDateTime;

    public boolean workIntervalInclude(LocalTime localTime) {
        return startTime.isBefore(localTime) && stopTime.isAfter(localTime);
    }

    public int compareTo(ScheduleUnit scheduleUnit){
        return priority.compareTo(scheduleUnit.getPriority());
    }

    public boolean isTimeToExecuteBy(LocalDateTime now) {
        if (lastExecutionDateTime == null) return true;
        return Duration.between(now, lastExecutionDateTime).abs().toMinutes() > 15;
    }

    public boolean sameByModuleName(SystemModule systemModule) {
        return Objects.equals(this.systemModule, systemModule);
    }

    @Builder
    public ScheduleUnit(
        UUID id,
        SystemModule systemModule,
        LocalTime startTime,
        LocalTime stopTime,
        Integer priority,
        LocalDateTime lastExecutionDateTime
    ) {
        super(id);
        this.systemModule = systemModule;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.priority = priority;
        this.lastExecutionDateTime = lastExecutionDateTime;
    }

    public void setLastExecutionLocalDateTime(LocalDateTime now) {
        lastExecutionDateTime = now;
    }
}
