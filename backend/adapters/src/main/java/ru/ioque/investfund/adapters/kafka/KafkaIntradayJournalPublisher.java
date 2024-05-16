package ru.ioque.investfund.adapters.kafka;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.IntradayJournalPublisher;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Component
@AllArgsConstructor
@Profile("!tests")
public class KafkaIntradayJournalPublisher implements IntradayJournalPublisher {
    KafkaTemplate<String, IntradayData> kafkaTemplate;

    public void publish(IntradayData intradayData) {
        kafkaTemplate.send("intraday-data", intradayData);
    }
}
