package ru.ioque.acceptance.adapters.other;

import org.springframework.stereotype.Component;
import ru.ioque.acceptance.application.adapters.DateTimeProvider;

import java.time.LocalDateTime;

@Component
public class DateTimeProviderImpl implements DateTimeProvider {
    LocalDateTime now = LocalDateTime.now();

    public LocalDateTime now() {
        return now;
    }

    public void initToday(LocalDateTime dateTime) {
        now = dateTime;
    }
}
