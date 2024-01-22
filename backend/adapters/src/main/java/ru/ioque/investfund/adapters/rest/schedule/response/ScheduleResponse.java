package ru.ioque.investfund.adapters.rest.schedule.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.domain.schedule.Schedule;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    LocalDateTime dateTime;
    List<ScheduleUnitResponse> scheduleUnits;

    public static ScheduleResponse fromDomain(Schedule schedule) {
        return ScheduleResponse
            .builder()
            .scheduleUnits(
                schedule
                    .getScheduleUnits()
                    .map(ScheduleUnitResponse::fromDomain)
                    .toList()
            )
            .build();
    }
}
