package ru.ioque.investfund.adapters.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.integration.event.IntegrationEvent;

@Slf4j
@Getter
@Component
public class FakeEventPublisher implements EventPublisher {
    @Override
    public void publish(IntegrationEvent event) {
        log.info("Received event: {}", event);
    }
}
