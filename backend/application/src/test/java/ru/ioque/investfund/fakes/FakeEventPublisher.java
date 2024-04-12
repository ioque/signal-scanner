package ru.ioque.investfund.fakes;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.domain.core.DomainEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class FakeEventPublisher implements EventPublisher {
    @Getter
    List<DomainEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void publish(DomainEvent event) {
        if (events.stream().filter(row -> row.getId().equals(event.getId())).findFirst().isEmpty()) {
            events.add(event);
        }
    }
}
