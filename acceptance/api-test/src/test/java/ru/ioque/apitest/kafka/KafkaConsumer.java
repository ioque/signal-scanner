package ru.ioque.apitest.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

@Getter
@Setter
@Component
public class KafkaConsumer {
    ConcurrentLinkedQueue<String> messages;
    private CountDownLatch latch = new CountDownLatch(1);

    public KafkaConsumer() {
        this.messages = new ConcurrentLinkedQueue<>();
    }


    @KafkaListener(topics = "events", groupId = "acceptance")
    public void listenAudit(String message) {
        latch.countDown();
        messages.add(message);
    }

    public void initDefaultLatch() {
        latch = new CountDownLatch(1);
    }

}
