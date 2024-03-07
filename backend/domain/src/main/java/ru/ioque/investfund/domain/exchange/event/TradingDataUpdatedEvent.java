package ru.ioque.investfund.domain.exchange.event;

import ru.ioque.investfund.domain.core.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class TradingDataUpdatedEvent extends DomainEvent {
    public TradingDataUpdatedEvent(UUID id, LocalDateTime dateTime) {
        super(id, dateTime);
    }
}
