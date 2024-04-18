package ru.ioque.investfund.domain.datasource.value;

import java.util.List;
import java.util.OptionalLong;

public class IntradayBatch {
    private final List<IntradayValue> values;

    public IntradayBatch(List<IntradayValue> values) {
        this.values = values;
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
