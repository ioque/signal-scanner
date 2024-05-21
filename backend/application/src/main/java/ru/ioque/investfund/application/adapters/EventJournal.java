package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.integration.IntegrationEvent;

public interface EventJournal {
    void publish(IntegrationEvent event);
}
