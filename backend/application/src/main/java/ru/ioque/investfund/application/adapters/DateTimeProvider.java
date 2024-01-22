package ru.ioque.investfund.application.adapters;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DateTimeProvider {
    LocalDateTime nowDateTime();
    LocalDate nowDate();
    void initToday(LocalDateTime today);
    LocalDate daysAgo(int days);
    LocalDate monthsAgo(int months);
}
