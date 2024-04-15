package ru.ioque.investfund.adapters.kafka;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.domain.core.DomainEvent;

@Component
@AllArgsConstructor
@Profile("!adapter-tests")
public class KafkaEventPublisher implements EventPublisher {
    KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Override
    public void publish(DomainEvent event) {
        kafkaTemplate.send("events", event);
    }
}
