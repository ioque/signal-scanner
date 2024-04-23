package ru.ioque.investfund.adapters.kafka;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandPublisher;
import ru.ioque.investfund.application.Command;

@Component
@AllArgsConstructor
@Profile("!tests")
public class KafkaCommandPublisher implements CommandPublisher {
    KafkaTemplate<String, Command> kafkaTemplate;

    @Override
    public void publish(Command command) {
        kafkaTemplate.send("commands", command);
    }
}
