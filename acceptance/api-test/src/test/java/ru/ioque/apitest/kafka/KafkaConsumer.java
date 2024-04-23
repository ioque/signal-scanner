package ru.ioque.apitest.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class KafkaConsumer {
    List<IntegrationEvent> messages = new ArrayList<>();
    boolean containsTradingDataIntegratedEvent = false;
    boolean containsSignalRegisteredEvent = false;
    boolean containsScanningFinishedEvent = false;

    @KafkaListener(topics = "events", groupId = "acceptance")
    public synchronized void consume(@Payload IntegrationEvent event) {
        if (event.isTradingDataIntegratedEvent()) {
            containsTradingDataIntegratedEvent = true;
        }
        if (event.isSignalRegisteredEvent()) {
            containsSignalRegisteredEvent = true;
        }
        if (event.isScanningFinishedEvent()) {
            containsScanningFinishedEvent = true;
        }
        messages.add(event);
    }

    public synchronized void clear() {
        messages.clear();
        containsTradingDataIntegratedEvent = false;
        containsSignalRegisteredEvent = false;
    }

    public synchronized boolean containsScanningFinishedEvent() {
        return containsScanningFinishedEvent;
    }

    public synchronized boolean containsTradingDataIntegratedEvent() {
        return containsTradingDataIntegratedEvent;
    }

    public synchronized boolean containsSignalRegisteredEvent() {
        return containsSignalRegisteredEvent;
    }
}
