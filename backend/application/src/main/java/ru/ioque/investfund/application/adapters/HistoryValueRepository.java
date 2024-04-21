package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.value.AggregatedHistory;

import java.util.Collection;

public interface HistoryValueRepository {
    void saveAll(Collection<AggregatedHistory> historyValues);
}
