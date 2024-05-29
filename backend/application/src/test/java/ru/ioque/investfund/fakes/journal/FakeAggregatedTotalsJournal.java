package ru.ioque.investfund.fakes.journal;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import ru.ioque.investfund.application.adapters.journal.AggregatedTotalsJournal;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public class FakeAggregatedTotalsJournal implements AggregatedTotalsJournal {
    private final Set<AggregatedTotals> histories = new HashSet<>();

    @Override
    public void publish(AggregatedTotals aggregatedTotals) {
        histories.add(aggregatedTotals);
    }

    @Override
    public List<AggregatedTotals> findAllBy(Ticker ticker) {
        return stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    @Override
    public Optional<AggregatedTotals> findActualBy(Ticker ticker) {
        return findAllBy(ticker).stream().max(AggregatedTotals::compareTo);
    }

    public Stream<AggregatedTotals> stream() {
        return histories.stream();
    }
}
