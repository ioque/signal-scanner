package ru.ioque.investfund.adapters.kafka.streaming;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.ioque.investfund.domain.datasource.value.InstrumentStatistic;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Configuration
public class SerdeConfiguration {
    @Bean
    public Serde<IntradayData> intradayDataSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(IntradayData.class));
    }

    @Bean
    public Serde<InstrumentStatistic> instrumentStatisticSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(InstrumentStatistic.class));
    }
}
