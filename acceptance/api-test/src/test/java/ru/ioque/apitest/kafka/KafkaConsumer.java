package ru.ioque.apitest.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@Setter
@Component
public class KafkaConsumer {
    ConcurrentLinkedQueue<DomainEvent> messages = new ConcurrentLinkedQueue<>();

    @KafkaListener(topics = "events", groupId = "acceptance")
    public void consume(@Payload DomainEvent event) {
        messages.add(event);
    }

    public void clear() {
        messages.clear();
    }
}
