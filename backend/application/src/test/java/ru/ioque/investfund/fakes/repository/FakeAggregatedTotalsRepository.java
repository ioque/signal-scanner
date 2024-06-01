package ru.ioque.investfund.fakes.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import ru.ioque.investfund.application.adapters.repository.AggregatedTotalsRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;

public class FakeAggregatedTotalsRepository implements AggregatedTotalsRepository {
    private final Set<AggregatedTotals> histories = new HashSet<>();

    @Override
    public void save(AggregatedTotals aggregatedTotals) {
        histories.add(aggregatedTotals);
    }

    @Override
    public List<AggregatedTotals> findAllBy(InstrumentId instrumentId) {
        return getAll().filter(row -> row.getInstrumentId().equals(instrumentId)).toList();
    }

    @Override
    public Optional<AggregatedTotals> findActualBy(InstrumentId instrumentId) {
        return findAllBy(instrumentId).stream().max(AggregatedTotals::compareTo);
    }

    public Stream<AggregatedTotals> getAll() {
        return histories.stream();
    }
}
