package ru.ioque.investfund.adapters.kafka.config;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Configuration
@Profile("!tests")
public class SerdeConfiguration {
    @Bean
    public Serde<IntradayData> intradayDataSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(IntradayData.class));
    }

    @Bean
    public Serde<IntradayPerformance> intradayStatistic() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(IntradayPerformance.class));
    }

    @Bean
    public Serde<AggregatedHistory> aggregatedHistorySerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(AggregatedHistory.class));
    }
}
