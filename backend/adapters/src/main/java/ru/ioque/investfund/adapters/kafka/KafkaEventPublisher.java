package ru.ioque.investfund.adapters.kafka;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.integration.event.IntegrationEvent;

@Component
@AllArgsConstructor
@Profile("!tests")
public class KafkaEventPublisher implements EventPublisher {
    KafkaTemplate<String, IntegrationEvent> kafkaTemplate;

    @Override
    public void publish(IntegrationEvent event) {
        kafkaTemplate.send("events", event);
    }
}
