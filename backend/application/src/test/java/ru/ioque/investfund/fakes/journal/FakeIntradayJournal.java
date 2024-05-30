package ru.ioque.investfund.fakes.journal;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.ioque.investfund.application.adapters.journal.IntradayJournal;
import ru.ioque.investfund.application.modules.pipeline.core.Processor;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Slf4j
public class FakeIntradayJournal implements IntradayJournal {
    public final Set<IntradayData> intradayDataList = new HashSet<>();
    private final Sinks.Many<IntradayData> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void publish(IntradayData intradayData) {
        intradayDataList.add(intradayData);
        sink.tryEmitNext(intradayData);
    }

    @Override
    public Flux<IntradayData> stream() {
        return sink.asFlux();
    }

    public Stream<IntradayData> getAll() {
        return intradayDataList.stream();
    }
}
