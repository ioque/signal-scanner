package ru.ioque.acceptance.application.adapters;

import java.time.LocalDateTime;

public interface DateTimeProvider {
    LocalDateTime now();
    void initToday(LocalDateTime today);
}
