package ru.ioque.investfund.adapters.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.AggregatedTotalsRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Component
public class PsqlAggregatedTotalsRepository implements AggregatedTotalsRepository {
    private final Set<AggregatedTotals> histories = new HashSet<>();

    @Override
    public void save(AggregatedTotals aggregatedTotals) {
        histories.add(aggregatedTotals);
    }

    @Override
    public List<AggregatedTotals> findAllBy(InstrumentId instrumentId) {
        return List.of();
    }

    @Override
    public Optional<AggregatedTotals> findActualBy(InstrumentId instrumentId) {
        return Optional.empty();
    }
}
