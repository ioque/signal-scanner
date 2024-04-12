package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.core.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
