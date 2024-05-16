package ru.ioque.investfund.fakes;

import lombok.extern.slf4j.Slf4j;
import ru.ioque.investfund.application.adapters.IntradayJournalPublisher;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Slf4j
public class FakeIntradayJournalPublisher implements IntradayJournalPublisher {
    @Override
    public void publish(IntradayData intradayData) {
        log.info(intradayData.toString());
    }
}
