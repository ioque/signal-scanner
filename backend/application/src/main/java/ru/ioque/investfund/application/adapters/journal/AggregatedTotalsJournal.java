package ru.ioque.investfund.application.adapters.journal;

import java.util.List;
import java.util.Optional;

import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public interface AggregatedTotalsJournal {
    void publish(AggregatedTotals aggregatedTotals);
    List<AggregatedTotals> findAllBy(Ticker ticker);
    Optional<AggregatedTotals> findActualBy(Ticker ticker);
}
