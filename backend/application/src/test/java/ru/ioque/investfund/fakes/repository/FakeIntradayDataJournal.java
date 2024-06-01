package ru.ioque.investfund.fakes.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.ioque.investfund.application.adapters.IntradayDataJournal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Slf4j
public class FakeIntradayDataJournal implements IntradayDataJournal {
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

    public List<IntradayData> getAll() {
        return intradayDataList.stream().toList();
    }
}
