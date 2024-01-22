package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.storage.jpa.entity.ScheduleUnitEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ScheduleUnitRepository;
import ru.ioque.investfund.application.adapters.ScheduleRepository;
import ru.ioque.investfund.domain.schedule.Schedule;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JpaScheduleRepository implements ScheduleRepository {
    ScheduleUnitRepository scheduleUnitRepository;

    @Override
    @Transactional(readOnly = true)
    public Schedule getSchedule() {
        return Schedule.from(scheduleUnitRepository.findAll().stream().map(ScheduleUnitEntity::toDomain).toList());
    }


    @Override
    @Transactional
    public void save(Schedule schedule) {
        scheduleUnitRepository.saveAll(schedule.getScheduleUnits().map(ScheduleUnitEntity::fromDomain).toList());
    }

    @Override
    public void delete() {
        scheduleUnitRepository.deleteAll();
    }
}
