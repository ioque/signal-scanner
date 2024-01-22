package ru.ioque.investfund.adapters.rest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.schedule.response.ScheduleResponse;
import ru.ioque.investfund.application.modules.schedule.ScheduleManager;
import ru.ioque.investfund.domain.schedule.Schedule;
import ru.ioque.investfund.domain.schedule.ScheduleUnit;
import ru.ioque.investfund.domain.schedule.SystemModule;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SCHEDULE REST INTERFACE")
public class ScheduleControllerTest extends BaseControllerTest {
    @Autowired
    ScheduleManager scheduleManager;
    @Test
    @SneakyThrows
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту GET /api/v1/schedule.
        """)
    public void testCase1() {
        Schedule schedule = new Schedule(
            List.of(
                ScheduleUnit.builder()
                    .id(UUID.randomUUID())
                    .systemModule(SystemModule.EXCHANGE)
                    .startTime(LocalTime.parse("10:00:00"))
                    .stopTime(LocalTime.parse("19:00:00"))
                    .priority(1)
                    .lastExecutionDateTime(LocalDateTime.now())
                    .build(),
                ScheduleUnit.builder()
                    .id(UUID.randomUUID())
                    .systemModule(SystemModule.SIGNAL_SCANNER)
                    .startTime(LocalTime.parse("10:00:00"))
                    .stopTime(LocalTime.parse("19:00:00"))
                    .priority(1)
                    .lastExecutionDateTime(LocalDateTime.now())
                    .build()
            )
        );
        Mockito
            .when(scheduleManager.getCurrentSchedule())
            .thenReturn(schedule);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/schedule"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                ScheduleResponse.fromDomain(schedule)
                            )
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T2. Выполнение запроса по эндпоинту GET /api/v1/system-modules.
        """)
    public void testCase2() {
        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/system-modules"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                Arrays.asList(SystemModule.values())
                            )
                    )
            );
    }
}
