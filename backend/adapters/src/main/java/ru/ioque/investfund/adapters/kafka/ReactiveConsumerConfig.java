package ru.ioque.investfund.adapters.kafka;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.ReceiverOptions;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import static ru.ioque.investfund.adapters.kafka.TopicConfiguration.INTRADAY_DATA_TOPIC;

@Profile("kafka")
@Configuration
public class ReactiveConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(JsonDeserializer.TRUSTED_PACKAGES,"ru.ioque.investfund.*");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return config;
    }

    @Bean
    public ReceiverOptions<String, IntradayData> intradayDataConsumerOptions() {
        ReceiverOptions<String, IntradayData> basicReceiverOptions = ReceiverOptions.create(consumerConfigs());
        return basicReceiverOptions.subscription(Collections.singletonList(INTRADAY_DATA_TOPIC));

    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, IntradayData> intradayDataConsumer(
        ReceiverOptions<String, IntradayData> options
    ) {
        return new ReactiveKafkaConsumerTemplate<>(options);
    }
}
