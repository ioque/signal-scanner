package ru.ioque.core.client.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.client.JsonHttpClient;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ServiceHttpClient extends JsonHttpClient {
    public ServiceHttpClient(String apiUrl) {
        super(apiUrl);
    }

    @SneakyThrows
    public void clearState() {
        delete("/api/service/state");
    }

    @SneakyThrows
    public void initDateTime(LocalDateTime date) {
        post("/api/service/date-time", objectMapper.writeValueAsString(getInitDateTimeRequest(date)));
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
