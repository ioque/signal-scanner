package ru.ioque.acceptance.client.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.client.RestTemplateFacade;
import ru.ioque.acceptance.client.schedule.request.SaveScheduleRequest;
import ru.ioque.acceptance.client.schedule.response.Schedule;

@Component
@AllArgsConstructor
public class ScheduleRestClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    @SneakyThrows
    public void saveSchedule(SaveScheduleRequest request) {
        restTemplateFacade.post("/schedule", objectMapper.writeValueAsString(request));
    }

    public Schedule getSchedule() {
        return restTemplateFacade.get("/schedule", Schedule.class);
    }

    public void clearState() {
        restTemplateFacade.delete("/schedule");
    }
}
