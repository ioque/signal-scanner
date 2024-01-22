package ru.ioque.investfund.adapters.storage.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "schedule_unit")
@Entity(name = "ScheduleUnit")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleUnitEntity extends AbstractEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SystemModule systemModule;
    @Column(nullable = false)
    LocalTime startTime;
    @Column(nullable = false)
    LocalTime stopTime;
    @Column(nullable = false)
    Integer priority;
    LocalDateTime lastExecutionDateTime;

    @Builder
    public ScheduleUnitEntity(
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

    public ScheduleUnit toDomain() {
        return ScheduleUnit.builder()
            .id(getId())
            .systemModule(systemModule)
            .startTime(startTime)
            .stopTime(stopTime)
            .priority(priority)
            .lastExecutionDateTime(lastExecutionDateTime)
            .build();
    }

    public static ScheduleUnitEntity fromDomain(ScheduleUnit scheduleUnit) {
        return ScheduleUnitEntity.builder()
            .id(scheduleUnit.getId())
            .systemModule(scheduleUnit.getSystemModule())
            .startTime(scheduleUnit.getStartTime())
            .stopTime(scheduleUnit.getStopTime())
            .priority(scheduleUnit.getPriority())
            .lastExecutionDateTime(scheduleUnit.getLastExecutionDateTime())
            .build();
    }
}
