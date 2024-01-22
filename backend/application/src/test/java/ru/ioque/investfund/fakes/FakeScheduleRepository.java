package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ScheduleRepository;
import ru.ioque.investfund.domain.schedule.Schedule;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;

import java.util.ArrayList;
import java.util.List;

public class FakeScheduleRepository implements ScheduleRepository {
    List<ScheduleUnit> scheduleUnits = new ArrayList<>();

    @Override
    public Schedule getSchedule() {
        if (scheduleUnits.isEmpty()) return Schedule.empty();
        return Schedule.from(scheduleUnits);
    }

    @Override
    public void save(Schedule schedule) {
        scheduleUnits.clear();
        scheduleUnits.addAll(schedule.getScheduleUnits().toList());
    }

    @Override
    public void delete() {
        scheduleUnits.clear();
    }
}
