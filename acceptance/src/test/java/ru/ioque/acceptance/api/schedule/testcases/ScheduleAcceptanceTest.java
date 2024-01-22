package ru.ioque.acceptance.api.schedule.testcases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ioque.acceptance.client.schedule.ScheduleRestClient;
import ru.ioque.acceptance.client.schedule.request.SaveScheduleRequest;
import ru.ioque.acceptance.client.schedule.request.SaveScheduleUnitRequest;
import ru.ioque.acceptance.client.schedule.response.Schedule;
import ru.ioque.acceptance.client.schedule.response.SystemModuleCode;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("МОДУЛЬ \"РАСПИСАНИЕ\"")
public class ScheduleAcceptanceTest {
    @Autowired
    ScheduleRestClient scheduleRestClient;

    @BeforeEach
    void beforeEach() {
        scheduleRestClient.clearState();
    }

    @Test
    @DisplayName("""
        T1. Создание пустых юнитов.
        """)
    void testCase1() {
        final SaveScheduleRequest scheduleRequest = new SaveScheduleRequest(
            List.of(
                buildScheduleUnitWith().systemModuleCode(SystemModuleCode.AGGREGATOR).build(),
                buildScheduleUnitWith().systemModuleCode(SystemModuleCode.SCANNER).build()
            )
        );

        final Schedule schedule = saveSchedule(scheduleRequest);

        assertEquals(2, schedule.getScheduleUnits().size());
    }

    private static SaveScheduleUnitRequest.SaveScheduleUnitRequestBuilder buildScheduleUnitWith() {
        return SaveScheduleUnitRequest.builder()
            .objectIds(List.of())
            .startTime(LocalTime.parse("00:00"))
            .stopTime(LocalTime.parse("23:59"))
            .priority(1);
    }

    private Schedule saveSchedule(SaveScheduleRequest scheduleRequest) {
        scheduleRestClient.saveSchedule(scheduleRequest);
        return scheduleRestClient.getSchedule();
    }
}
