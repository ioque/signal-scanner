package ru.ioque.investfund.adapters.datetime;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class RollingDateTimeProvider implements DateTimeProvider {
    LocalDateTime today;
    @Override
    public LocalDateTime nowDateTime() {
        if (today != null) return today;
        return LocalDateTime.now();
    }

    @Override
    public LocalDate nowDate() {
        return nowDateTime().toLocalDate();
    }

    @Override
    public void initToday(LocalDateTime today) {
        this.today = today;
    }

    @Override
    public LocalDate daysAgo(int days) {
        return nowDateTime().toLocalDate().minusDays(days);
    }

    @Override
    public LocalDate monthsAgo(int months) {
        return nowDateTime().toLocalDate().minusMonths(months);
    }
}
