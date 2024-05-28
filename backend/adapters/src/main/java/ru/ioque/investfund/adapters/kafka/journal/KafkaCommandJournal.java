package ru.ioque.investfund.adapters.kafka.journal;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.modules.api.Command;

@Component
@AllArgsConstructor
@Profile("!tests")
public class KafkaCommandJournal implements CommandJournal {
    KafkaTemplate<String, Command> kafkaTemplate;

    @Override
    public void publish(Command command) {
        kafkaTemplate.send("commands", command);
    }
}