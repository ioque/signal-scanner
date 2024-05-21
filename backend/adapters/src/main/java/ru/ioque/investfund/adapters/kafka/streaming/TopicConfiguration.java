package ru.ioque.investfund.adapters.kafka.streaming;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("!tests")
public class TopicConfiguration {
    public static final String INTRADAY_DATA_TOPIC = "intraday-data-topic";
    public static final String INTRADAY_STATISTIC_TOPIC = "intraday-statistic-topic";

    @Bean
    public NewTopic intradayDataTopic() {
        return TopicBuilder
                .name(INTRADAY_DATA_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic intradayStatisticTopic() {
        return TopicBuilder
                .name(INTRADAY_STATISTIC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
