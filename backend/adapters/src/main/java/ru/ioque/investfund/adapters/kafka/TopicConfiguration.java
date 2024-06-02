package ru.ioque.investfund.adapters.kafka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

@Configuration
@Profile("kafka")
public class TopicConfiguration {
    public static final String INTRADAY_DATA_TOPIC = "intraday-data-topic";
    public static final String BUSINESS_LOG_TOPIC = "business-log-topic";
    public static final String TECHNICAL_LOG_TOPIC = "technical-log-topic";

    @Bean
    public NewTopic intradayDataTopic() {
        return TopicBuilder
            .name(INTRADAY_DATA_TOPIC)
            .partitions(1)
            .replicas(1)
            .build();
    }


    @Bean
    public NewTopic businessLogTopic() {
        return TopicBuilder
            .name(BUSINESS_LOG_TOPIC)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic techLogTopic() {
        return TopicBuilder
            .name(TECHNICAL_LOG_TOPIC)
            .partitions(1)
            .replicas(1)
            .build();
    }
}
