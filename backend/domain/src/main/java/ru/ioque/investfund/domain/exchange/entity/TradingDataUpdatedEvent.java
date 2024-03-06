package ru.ioque.investfund.domain.exchange.entity;

import ru.ioque.investfund.domain.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class TradingDataUpdatedEvent extends DomainEvent {
    public TradingDataUpdatedEvent(UUID id, LocalDateTime dateTime) {
        super(id, dateTime);
    }
}
