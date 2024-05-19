package ru.ioque.investfund.adapters.kafka.streaming;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfiguration {
    public static final String INTRADAY_DATA_TOPIC = "intraday-data-topic";
    public static final String INSTRUMENT_STATISTIC_TOPIC = "instrument-statistics-topic";

    @Bean
    public NewTopic intradayDataTopic() {
        return TopicBuilder
                .name(INTRADAY_DATA_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic instrumentStatisticsTopic() {
        return TopicBuilder
                .name(INSTRUMENT_STATISTIC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
