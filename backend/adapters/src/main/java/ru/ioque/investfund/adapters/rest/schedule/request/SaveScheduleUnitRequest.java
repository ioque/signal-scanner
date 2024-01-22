package ru.ioque.investfund.adapters.rest.schedule.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveScheduleUnitRequest implements Serializable {
    UUID id;
    SystemModule systemModuleCode;
    LocalTime startTime;
    LocalTime stopTime;
    Integer priority;

    public ScheduleUnit toDomain(UUID id) {
        return ScheduleUnit.builder()
            .id(Optional.ofNullable(id).orElse(id))
            .startTime(startTime)
            .systemModule(systemModuleCode)
            .stopTime(stopTime)
            .priority(priority)
            .build();
    }
}
