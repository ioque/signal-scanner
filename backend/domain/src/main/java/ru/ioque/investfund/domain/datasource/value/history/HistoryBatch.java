package ru.ioque.investfund.domain.datasource.value.history;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.Optional;
import java.util.TreeSet;

public class HistoryBatch {
    private final TreeSet<@Valid HistoryValue> values;

    public HistoryBatch(TreeSet<HistoryValue> values) {
        this.values = values;
    }

    public Optional<LocalDate> getLastDate() {
        return values
            .stream()
            .max(HistoryValue::compareTo)
            .map(HistoryValue::getTradeDate);
    }
}
