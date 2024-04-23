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
    boolean containsTradingStateChanged = false;
    boolean containsTradingDataIntegrated = false;
    boolean containsSignalRegistered = false;
    boolean containsDatasourceScanned = false;

    @KafkaListener(topics = "events", groupId = "acceptance")
    public synchronized void consume(@Payload IntegrationEvent event) {
        if (event.isTradingStateChanged()) {
            containsTradingStateChanged = true;
        }
        if (event.isTradingDataIntegrated()) {
            containsTradingDataIntegrated = true;
        }
        if (event.isSignalRegistered()) {
            containsSignalRegistered = true;
        }
        if (event.isDatasourceScanned()) {
            containsDatasourceScanned = true;
        }
        messages.add(event);
    }

    public synchronized void clear() {
        messages.clear();
        containsTradingStateChanged = false;
        containsTradingDataIntegrated = false;
        containsSignalRegistered = false;
        containsDatasourceScanned = false;
    }

    public synchronized boolean containsDatasourceScanned() {
        return containsDatasourceScanned;
    }

    public synchronized boolean containsTradingDataIntegrated() {
        return containsTradingDataIntegrated;
    }

    public synchronized boolean containsSignalRegistered() {
        return containsSignalRegistered;
    }

    public synchronized boolean containsTradingStateChanged() {
        return containsTradingStateChanged;
    }
}
