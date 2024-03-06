package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.DomainEvent;

public interface EventBus {
    void publish(DomainEvent event);
}
