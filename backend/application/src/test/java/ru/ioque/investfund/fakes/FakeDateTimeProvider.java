package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FakeDateTimeProvider implements DateTimeProvider {
    @Setter
    private LocalDateTime now = LocalDateTime.now();

    @Override
    public LocalDateTime nowDateTime() {
        return now;
    }

    @Override
    public LocalDate nowDate() {
        return nowDateTime().toLocalDate();
    }

    @Override
    public void initToday(LocalDateTime today) {
        setNow(today);
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
