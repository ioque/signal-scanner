package ru.ioque.investfund.adapters.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.ioque.investfund.application.CommandBus;
import ru.ioque.investfund.application.integration.EventBus;
import ru.ioque.investfund.domain.core.Command;
import ru.ioque.investfund.domain.core.DomainEvent;

@Slf4j
@Service
@AllArgsConstructor
@Profile("!tests")
public class KafkaConsumer {
    private final CommandBus commandBus;
    private final EventBus eventBus;

    @KafkaListener(topics = "commands")
    public void processCommand(@Payload Command command) {
        commandBus.execute(command);
    }

    @KafkaListener(topics = "events")
    public void process(@Payload DomainEvent event) {
        eventBus.process(event);
    }
}
