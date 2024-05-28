package ru.ioque.investfund.application.adapters.journal;

import java.util.List;
import java.util.Optional;

import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public interface AggregatedHistoryJournal {
    void publish(AggregatedHistory aggregatedHistory);
    List<AggregatedHistory> findAllBy(Ticker ticker);
    Optional<AggregatedHistory> findActualBy(Ticker ticker);
}
