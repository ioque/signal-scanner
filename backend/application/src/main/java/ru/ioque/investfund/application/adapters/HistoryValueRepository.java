package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.value.HistoryValue;

import java.util.List;

public interface HistoryValueRepository {
    void saveAll(List<HistoryValue> historyValues);
}
