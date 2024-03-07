package ru.ioque.investfund.fakes;

import lombok.Getter;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.domain.core.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeEventBus implements EventBus {
    @Getter
    List<DomainEvent> events = new CopyOnWriteArrayList<>();
    Map<SystemModule, List<Class<? extends DomainEvent>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void publish(DomainEvent event) {
        if (events.stream().filter(row -> row.getId().equals(event.getId())).findFirst().isEmpty()) {
            events.add(event);
            subscribers
                .entrySet()
                .stream()
                .filter(subscriber -> subscriber.getValue().contains(event.getClass()))
                .forEach(subscriber -> subscriber.getKey().execute());
        }
    }

    @Override
    public void subscribe(Class<? extends DomainEvent> eventType, SystemModule systemModule) {
        if (!subscribers.containsKey(systemModule)) {
            subscribers.put(systemModule, new ArrayList<>());
        }
        if (subscribers.get(systemModule).contains(eventType)) {
            return;
        }
        subscribers.get(systemModule).add(eventType);
    }
}
