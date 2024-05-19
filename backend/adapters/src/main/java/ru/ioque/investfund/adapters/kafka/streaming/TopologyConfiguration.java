package ru.ioque.investfund.adapters.kafka.streaming;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ioque.investfund.domain.datasource.value.InstrumentStatistic;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import static ru.ioque.investfund.adapters.kafka.streaming.TopicConfiguration.INSTRUMENT_STATISTIC_TOPIC;
import static ru.ioque.investfund.adapters.kafka.streaming.TopicConfiguration.INTRADAY_DATA_TOPIC;

@Configuration
public class TopologyConfiguration {
    @Autowired
    private Serde<IntradayData> intradayDataSerde;
    @Autowired
    private Serde<InstrumentStatistic> instrumentStatisticSerde;

    @Bean
    public Topology createTopology(StreamsBuilder builder) {
        final Serde<String> tickerSerde = Serdes.String();
        final var intraday = builder
                .stream(
                        INTRADAY_DATA_TOPIC,
                        Consumed.with(tickerSerde, intradayDataSerde)
                );
        final var statistic = intraday
                .groupByKey()
                .aggregate(
                        InstrumentStatistic::empty,
                        (k, v, a) -> a.add(k, v),
                        Materialized.with(
                                tickerSerde,
                                instrumentStatisticSerde
                        )
                );
        statistic
                .toStream()
                .to(INSTRUMENT_STATISTIC_TOPIC, Produced.with(tickerSerde, instrumentStatisticSerde));

        Topology topology = builder.build();

        System.out.println("===============================");
        System.out.println(topology.describe());
        System.out.println("===============================");

        return topology;
    }
}
