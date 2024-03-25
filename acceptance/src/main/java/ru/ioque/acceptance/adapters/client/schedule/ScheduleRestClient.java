package ru.ioque.acceptance.adapters.client.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.schedule.request.SaveScheduleRequest;
import ru.ioque.acceptance.adapters.client.schedule.response.Schedule;
import ru.ioque.acceptance.adapters.client.RestTemplateFacade;

@Component
@AllArgsConstructor
public class ScheduleRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    @SneakyThrows
    public void saveSchedule(SaveScheduleRequest request) {
        restTemplateFacade.post("/api/schedule", objectMapper.writeValueAsString(request));
    }

    public Schedule getSchedule() {
        return restTemplateFacade.get("/api/schedule", Schedule.class);
    }
}
