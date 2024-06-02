package ru.ioque.investfund.fakes.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import ru.ioque.investfund.application.adapters.repository.AggregatedTotalsRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;

public class FakeAggregatedTotalsRepository implements AggregatedTotalsRepository {
    private final Map<InstrumentId, TreeSet<AggregatedTotals>> histories = new HashMap<>();

    @Override
    public void saveAll(List<AggregatedTotals> aggregatedTotals) {
        for (AggregatedTotals aggregatedTotal : aggregatedTotals) {
            if (!histories.containsKey(aggregatedTotal.getInstrumentId())) {
                histories.put(aggregatedTotal.getInstrumentId(), new TreeSet<>());
            }
            histories.get(aggregatedTotal.getInstrumentId()).add(aggregatedTotal);
        }
    }

    @Override
    public List<AggregatedTotals> findAllBy(InstrumentId instrumentId) {
        return histories.getOrDefault(instrumentId, new TreeSet<>()).stream().toList();
    }

    @Override
    public Optional<AggregatedTotals> findActualBy(InstrumentId instrumentId) {
        TreeSet<AggregatedTotals> aggregatedTotals = histories.get(instrumentId);
        if (aggregatedTotals == null || aggregatedTotals.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(aggregatedTotals.last());
    }
}
