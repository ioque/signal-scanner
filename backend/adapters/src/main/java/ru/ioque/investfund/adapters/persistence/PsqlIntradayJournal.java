package ru.ioque.investfund.adapters.persistence;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.ioque.investfund.application.adapters.journal.IntradayJournal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Component
public class PsqlIntradayJournal implements IntradayJournal {
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
}
