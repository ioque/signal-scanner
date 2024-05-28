package ru.ioque.investfund.adapters.kafka.config;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.context.annotation.Profile;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import static ru.ioque.investfund.adapters.kafka.config.TopicConfiguration.INTRADAY_STATISTIC_TOPIC;
import static ru.ioque.investfund.adapters.kafka.config.TopicConfiguration.INTRADAY_DATA_TOPIC;

@Slf4j
@Configuration
@Profile("!tests")
public class TopologyConfiguration {
    @Autowired
    private Serde<IntradayData> intradayDataSerde;
    @Autowired
    private Serde<IntradayPerformance> intradayStatistic;

    @Bean
    public Topology createTopology(StreamsBuilder builder) {
        final Serde<String> tickerSerde = Serdes.String();
        final var intraday = builder
                .stream(
                        INTRADAY_DATA_TOPIC,
                        Consumed.with(tickerSerde, intradayDataSerde)
                );
        final var statistics = intraday
                .groupByKey()
                .aggregate(
                        IntradayPerformance::empty,
                        (k, v, a) -> a.add(k, v),
                        Materialized.with(
                                tickerSerde,
                            intradayStatistic
                        )
                );
        statistics
                .toStream()
                .to(INTRADAY_STATISTIC_TOPIC, Produced.with(tickerSerde, intradayStatistic));

        Topology topology = builder.build();
        log.info(topology.describe().toString());
        return topology;
    }
}
