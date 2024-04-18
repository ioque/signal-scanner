package ru.ioque.investfund.domain.datasource.value;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HistoryBatch {
    @Getter
    private final String ticker;
    private final List<HistoryValue> values;

    public HistoryBatch(String ticker, List<HistoryValue> values) {
        this.ticker = ticker;
        this.values = values;
        if (values.stream().anyMatch(row -> !row.getInstrumentId().getTicker().equals(ticker))) {
            throw new IllegalArgumentException("History batch must contain values with same ticker.");
        }
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
