package ru.ioque.investfund.domain.schedule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

//идентификация, а нужна ли? этот агрегат не имеет смысла в постоянном хранилище
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Schedule implements Serializable {
    Map<SystemModule, ScheduleUnit> scheduleUnits = new HashMap<>();

    public Schedule(List<ScheduleUnit> scheduleUnits) {
        scheduleUnits.forEach(row -> this.scheduleUnits.put(row.getSystemModule(), row));
    }

    public Stream<ScheduleUnit> getScheduleUnits() {
        return scheduleUnits.values().stream();
    }

    public static Schedule empty() {
        return new Schedule(new ArrayList<>());
    }

    public static Schedule from(List<ScheduleUnit> scheduleUnits) {
        return new Schedule(scheduleUnits);
    }

    public Stream<ScheduleUnit> getActualScheduleUnits(LocalDateTime today) {
        return scheduleUnits
            .values()
            .stream()
            .filter(row -> row.workIntervalInclude(today.toLocalTime()))
            .sorted();
    }

    public void saveScheduleUnit(ScheduleUnit scheduleUnit) {
        scheduleUnits.put(scheduleUnit.getSystemModule(), scheduleUnit);
    }

    public Optional<ScheduleUnit> getScheduleUnitBy(SystemModule systemModule) {
        return scheduleUnits.values().stream().filter(row -> row.sameByModuleName(systemModule)).findFirst();
    }
}
