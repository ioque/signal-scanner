package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.ScheduleRepository;
import ru.ioque.investfund.domain.schedule.Schedule;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("JPA_SCHEDULE_REPO")
public class JpaScheduleRepositoryTest extends BaseJpaTest {
    ScheduleRepository scheduleRepository;
    ExchangeRepository exchangeRepository;
    ScannerRepository scannerRepository;

    public JpaScheduleRepositoryTest(
        @Autowired ScheduleRepository scheduleRepository,
        @Autowired ExchangeRepository exchangeRepository,
        @Autowired ScannerRepository scannerRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.exchangeRepository = exchangeRepository;
        this.scannerRepository = scannerRepository;
    }

    @Test
    @DisplayName("""
        T1. Сохранение расписания в постоянное хранилище.
        """)
    void test1() {
        scheduleRepository.save(Schedule.from(
            List.of(buildScheduleUnit().systemModule(SystemModule.EXCHANGE).build(), buildScheduleUnit().systemModule(SystemModule.SIGNAL_SCANNER).build())
        ));

        assertEquals(2, scheduleRepository.getSchedule().getScheduleUnits().count());
    }

    @Test
    @DisplayName("""
        T1. Сохранение расписания с датой исполнения в постоянное хранилище.
        """)
    void test2() {
        scheduleRepository.save(Schedule.from(
            List.of(buildScheduleUnit().lastExecutionDateTime(LocalDateTime.now()).systemModule(SystemModule.EXCHANGE).build(), buildScheduleUnit().lastExecutionDateTime(LocalDateTime.now()).systemModule(SystemModule.SIGNAL_SCANNER).build())
        ));

        assertEquals(2, scheduleRepository.getSchedule().getScheduleUnits().count());
        assertNotNull(scheduleRepository
            .getSchedule().getScheduleUnitBy(SystemModule.EXCHANGE).orElseThrow().getLastExecutionDateTime());
        assertNotNull(scheduleRepository
            .getSchedule().getScheduleUnitBy(SystemModule.SIGNAL_SCANNER).orElseThrow().getLastExecutionDateTime());
    }
}
