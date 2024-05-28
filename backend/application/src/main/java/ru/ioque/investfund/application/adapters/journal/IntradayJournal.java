package ru.ioque.investfund.application.adapters.journal;

import java.util.Optional;

import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public interface IntradayJournal {
    void publish(IntradayData intradayData);
    Optional<IntradayData> findBy(Ticker ticker);
}
