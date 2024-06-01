package ru.ioque.investfund.adapters.kafka;

import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Configuration
public class ReactiveKafkaProducerConfig {
    @Bean
    public ReactiveKafkaProducerTemplate<String, IntradayData> reactiveKafkaProducerTemplate(
        KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<String, IntradayData>(SenderOptions.create(props));
    }
}
