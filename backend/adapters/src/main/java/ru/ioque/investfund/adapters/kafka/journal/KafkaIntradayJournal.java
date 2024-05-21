package ru.ioque.investfund.adapters.kafka.journal;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.IntradayJournal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import static ru.ioque.investfund.adapters.kafka.streaming.TopicConfiguration.INTRADAY_DATA_TOPIC;

@Component
@Profile("!tests")
@AllArgsConstructor
public class KafkaIntradayJournal implements IntradayJournal {
    KafkaTemplate<String, IntradayData> kafkaTemplate;

    public void publish(IntradayData intradayData) {
        kafkaTemplate.send(INTRADAY_DATA_TOPIC,
                intradayData.getTicker().getValue(),
                intradayData);
    }
}
