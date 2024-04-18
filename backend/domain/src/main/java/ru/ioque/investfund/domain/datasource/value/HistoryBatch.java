package ru.ioque.investfund.domain.datasource.value;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HistoryBatch {
    private final List<HistoryValue> values;

    public HistoryBatch(List<HistoryValue> values) {
        this.values = values;
    }

    public List<HistoryValue> getUniqueValues() {
        return values.stream().distinct().toList();
    }

    public Optional<LocalDate> getLastDate() {
        return values
            .stream()
            .max(HistoryValue::compareTo)
            .map(HistoryValue::getTradeDate);
    }
}
