package ru.ioque.investfund.adapters.kafka.journal;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import static ru.ioque.investfund.adapters.kafka.config.TopicConfiguration.SIGNAL_TOPIC;

@Component
@Profile("!tests")
@AllArgsConstructor
public class KafkaSignalJournal implements SignalJournal {
    KafkaTemplate<String, Signal> kafkaTemplate;

    @Override
    public void publish(Signal signal) {
        kafkaTemplate.send(SIGNAL_TOPIC, signal.getScannerId().toString(), signal);
    }

    @Override
    public List<Signal> findActualBy(ScannerId scannerId) {
        return List.of();
    }
}
