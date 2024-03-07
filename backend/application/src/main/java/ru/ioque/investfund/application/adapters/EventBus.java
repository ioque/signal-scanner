package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.domain.core.DomainEvent;

public interface EventBus {
    void publish(DomainEvent event);
    void subscribe(Class<? extends DomainEvent> eventType, SystemModule systemModule);
}
