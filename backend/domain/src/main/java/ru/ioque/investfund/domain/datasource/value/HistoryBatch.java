package ru.ioque.investfund.domain.datasource.value;

import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class HistoryBatch {
    private final List<HistoryValue> values;

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
