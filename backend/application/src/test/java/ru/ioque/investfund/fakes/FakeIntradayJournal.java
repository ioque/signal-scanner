package ru.ioque.investfund.fakes;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import ru.ioque.investfund.application.adapters.journal.IntradayJournal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Slf4j
public class FakeIntradayJournal implements IntradayJournal {
    public Set<IntradayData> intradayDataList = new HashSet<>();

    @Override
    public void publish(IntradayData intradayData) {
        intradayDataList.add(intradayData);
    }

    public Stream<IntradayData> stream() {
        return intradayDataList.stream();
    }
}
