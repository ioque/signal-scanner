package ru.ioque.acceptance.api.schedule;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ioque.acceptance.adapters.client.schedule.ScheduleRestClient;
import ru.ioque.acceptance.adapters.client.schedule.request.SaveScheduleRequest;
import ru.ioque.acceptance.adapters.client.schedule.request.SaveScheduleUnitRequest;
import ru.ioque.acceptance.adapters.client.schedule.response.Schedule;
import ru.ioque.acceptance.api.BaseApiAcceptanceTest;

import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@DisplayName("МОДУЛЬ \"РАСПИСАНИЕ\"")
public class ScheduleAcceptanceTest extends BaseApiAcceptanceTest {
    @Autowired
    ScheduleRestClient scheduleRestClient;

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
