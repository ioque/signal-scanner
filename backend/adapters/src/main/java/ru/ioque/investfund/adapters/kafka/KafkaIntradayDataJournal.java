package ru.ioque.investfund.adapters.kafka;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.ioque.investfund.application.adapters.IntradayDataJournal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import static ru.ioque.investfund.adapters.kafka.TopicConfiguration.INTRADAY_DATA_TOPIC;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KafkaIntradayDataJournal implements IntradayDataJournal {
    ReactiveKafkaProducerTemplate<String, IntradayData> kafkaProducerTemplate;
    ReactiveKafkaConsumerTemplate<String, IntradayData> kafkaConsumerTemplate;

    @Override
    public void publish(IntradayData intradayData) {
        kafkaProducerTemplate
            .send(INTRADAY_DATA_TOPIC, intradayData.getTicker().getValue(), intradayData)
            .doOnSuccess(senderResult -> log.info("sent {} offset : {}", intradayData, senderResult.recordMetadata().offset()))
            .subscribe();
    }

    @Override
    public Flux<IntradayData> stream() {
        return kafkaConsumerTemplate.receiveAutoAck().map(ConsumerRecord::value);
    }
}
