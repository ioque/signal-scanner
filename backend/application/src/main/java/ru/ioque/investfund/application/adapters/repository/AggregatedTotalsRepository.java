package ru.ioque.investfund.application.adapters.repository;

import java.util.List;
import java.util.Optional;

import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;

public interface AggregatedTotalsRepository {
    void saveAll(List<AggregatedTotals> aggregatedTotals);
    List<AggregatedTotals> findAllBy(InstrumentId instrumentId);
    Optional<AggregatedTotals> findActualBy(InstrumentId instrumentId);
}
