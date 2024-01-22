package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.schedule.Schedule;

public interface ScheduleRepository {
    Schedule getSchedule();
    void save(Schedule schedule);
    void delete();
}
