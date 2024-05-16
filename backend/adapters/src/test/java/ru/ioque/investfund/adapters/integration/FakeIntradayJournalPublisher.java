package ru.ioque.investfund.adapters.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.IntradayJournalPublisher;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Slf4j
@Getter
@Component
public class FakeIntradayJournalPublisher implements IntradayJournalPublisher {

    @Override
    public void publish(IntradayData intradayData) {
        log.info(intradayData.toString());
    }
}
