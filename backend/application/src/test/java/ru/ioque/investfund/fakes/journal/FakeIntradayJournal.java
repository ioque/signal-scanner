package ru.ioque.investfund.fakes.journal;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import ru.ioque.investfund.application.adapters.journal.IntradayJournal;
import ru.ioque.investfund.application.adapters.journal.Processor;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Slf4j
public class FakeIntradayJournal implements IntradayJournal {
    public Set<Processor<IntradayData>> processors = new HashSet<>();
    public Set<IntradayData> intradayDataList = new HashSet<>();

    @Override
    public void publish(IntradayData intradayData) {
        intradayDataList.add(intradayData);
        processors.forEach(processor -> processor.process(intradayData));
    }

    @Override
    public void subscribe(Processor<IntradayData> processor) {
        processors.add(processor);
    }

    public Stream<IntradayData> stream() {
        return intradayDataList.stream();
    }
}
