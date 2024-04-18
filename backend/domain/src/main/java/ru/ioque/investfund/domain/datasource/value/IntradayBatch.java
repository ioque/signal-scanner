package ru.ioque.investfund.domain.datasource.value;

import lombok.Getter;

import java.util.List;
import java.util.OptionalLong;

public class IntradayBatch {
    @Getter
    private final String ticker;
    private final List<IntradayValue> values;

    public IntradayBatch(String ticker, List<IntradayValue> values) {
        this.ticker = ticker;
        this.values = values;
        if (values.stream().anyMatch(row -> !row.getInstrumentId().getTicker().equals(ticker))) {
            throw new IllegalArgumentException("Intraday batch must contain values with same ticker.");
        }
    }

    public List<IntradayValue> getUniqueValues() {
        return values.stream().distinct().toList();
    }

    public OptionalLong getLastNumber() {
        return values
            .stream()
            .mapToLong(IntradayValue::getNumber)
            .max();
    }
}
