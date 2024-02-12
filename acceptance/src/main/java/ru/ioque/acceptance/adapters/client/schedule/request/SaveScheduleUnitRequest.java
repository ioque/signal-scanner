package ru.ioque.acceptance.adapters.client.schedule.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.adapters.client.schedule.response.SystemModuleCode;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;
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
    SystemModuleCode systemModuleCode;
    List<UUID> objectIds;
    LocalTime startTime;
    LocalTime stopTime;
    Integer priority;
}
