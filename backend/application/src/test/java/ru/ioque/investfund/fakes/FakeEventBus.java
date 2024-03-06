package ru.ioque.investfund.fakes;

import lombok.Getter;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.domain.DomainEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeEventBus implements EventBus {
    @Getter
    List<DomainEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void publish(DomainEvent event) {
        if (events.stream().filter(row -> row.getId().equals(event.getId())).findFirst().isEmpty()) {
            events.add(event);
        }
    }
}
