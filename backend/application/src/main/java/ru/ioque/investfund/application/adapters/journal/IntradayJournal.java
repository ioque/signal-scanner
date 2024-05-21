package ru.ioque.investfund.application.adapters.journal;

import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

public interface IntradayJournal {
    void publish(IntradayData intradayData);
}
