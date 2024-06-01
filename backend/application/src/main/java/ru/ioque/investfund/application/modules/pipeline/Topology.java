package ru.ioque.investfund.application.modules.pipeline;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.ioque.investfund.application.modules.pipeline.core.Source;
import ru.ioque.investfund.application.modules.pipeline.sink.RiskManagerSink;
import ru.ioque.investfund.application.modules.pipeline.sink.SignalRegistry;
import ru.ioque.investfund.application.modules.pipeline.transformer.PerformanceCalculator;
import ru.ioque.investfund.application.modules.pipeline.transformer.SignalsFinder;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Component
public class Topology {

    public Topology(
        Source<IntradayData> intradayDataSource,
        PerformanceCalculator performanceCalculator,
        SignalsFinder signalsFinder,
        SignalRegistry signalRegistry,
        RiskManagerSink riskManagerSink) {

        final Flux<IntradayData> intradayDataStream = intradayDataSource.stream();
        intradayDataStream
            .map(performanceCalculator::transform)
            .map(signalsFinder::transform)
            .flatMap(Flux::fromIterable)
            .subscribe(signalRegistry::consume);
        intradayDataStream.subscribe(riskManagerSink::consume);
    }
}
