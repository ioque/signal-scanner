package ru.ioque.investfund.adapters.infrastructure;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.domain.core.DomainEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Component
public class FakeEventPublisher implements EventPublisher {
    private final List<DomainEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void publish(DomainEvent event) {
        events.add(event);
    }
}
