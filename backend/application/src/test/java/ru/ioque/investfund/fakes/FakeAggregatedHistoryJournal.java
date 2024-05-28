package ru.ioque.investfund.fakes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import ru.ioque.investfund.application.adapters.journal.AggregatedHistoryJournal;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public class FakeAggregatedHistoryJournal implements AggregatedHistoryJournal {
    private final Set<AggregatedHistory> histories = new HashSet<>();

    @Override
    public void publish(AggregatedHistory aggregatedHistory) {
        histories.add(aggregatedHistory);
    }

    @Override
    public List<AggregatedHistory> findAllBy(Ticker ticker) {
        return stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    @Override
    public Optional<AggregatedHistory> getBy(Ticker ticker) {
        return findAllBy(ticker).stream().max(AggregatedHistory::compareTo);
    }

    public Stream<AggregatedHistory> stream() {
        return histories.stream();
    }
}
