package ru.ioque.investfund.adapters.rest.schedule.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddNewScheduleUnitRequest {
    SystemModule systemModule;
    LocalTime startTime;
    LocalTime stopTime;
    Integer priority;
}
