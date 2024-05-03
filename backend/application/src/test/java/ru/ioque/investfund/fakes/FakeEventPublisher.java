package ru.ioque.investfund.fakes;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.integration.IntegrationEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Component
public class FakeEventPublisher implements EventPublisher {
    List<IntegrationEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void publish(IntegrationEvent event) {
        events.add(event);
    }
}
