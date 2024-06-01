package ru.ioque.investfund.adapters.kafka;

import java.util.Collections;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import static ru.ioque.investfund.adapters.kafka.TopicConfiguration.INTRADAY_DATA_TOPIC;

@Configuration
public class ReactiveKafkaConsumerConfig {
    @Bean
    public ReceiverOptions<String, IntradayData> kafkaReceiverOptions(KafkaProperties kafkaProperties) {
        ReceiverOptions<String, IntradayData> basicReceiverOptions = ReceiverOptions.create(
            kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(INTRADAY_DATA_TOPIC));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, IntradayData> reactiveKafkaConsumerTemplate(ReceiverOptions<String, IntradayData> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }
}
