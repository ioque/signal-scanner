package ru.ioque.investfund.domain.statistic.event;

import ru.ioque.investfund.domain.core.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class StatisticCalculatedEvent extends DomainEvent {
    public StatisticCalculatedEvent(UUID id, LocalDateTime dateTime) {
        super(id, dateTime);
    }
}
