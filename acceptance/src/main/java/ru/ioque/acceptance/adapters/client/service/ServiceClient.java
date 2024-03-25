package ru.ioque.acceptance.adapters.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.RestTemplateFacade;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@AllArgsConstructor
public class ServiceClient {
    RestTemplateFacade restTemplateFacade;
    ObjectMapper objectMapper;

    public void clearState() {
        restTemplateFacade.delete("/api/service/state");
    }

    @SneakyThrows
    public void initDateTime(LocalDateTime date) {
        restTemplateFacade.post("/api/service/date-time", objectMapper.writeValueAsString(getInitDateTimeRequest(date)));
    }

    private InitDateTimRequest getInitDateTimeRequest(LocalDateTime date) {
        return new InitDateTimRequest(date.toLocalDate(), date.toLocalTime());
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class InitDateTimRequest implements Serializable {
        LocalDate date;
        LocalTime time;
    }
}
