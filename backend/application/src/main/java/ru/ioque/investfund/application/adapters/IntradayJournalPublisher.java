package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

public interface IntradayJournalPublisher {
    void publish(IntradayData intradayData);
}
