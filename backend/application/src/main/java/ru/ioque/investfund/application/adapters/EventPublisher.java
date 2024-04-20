package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.integration.event.IntegrationEvent;

public interface EventPublisher {
    void publish(IntegrationEvent event);
}
