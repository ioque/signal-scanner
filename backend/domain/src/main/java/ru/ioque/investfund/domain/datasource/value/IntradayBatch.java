package ru.ioque.investfund.domain.datasource.value;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.OptionalLong;

@AllArgsConstructor
public class IntradayBatch {
    private final List<IntradayValue> values;

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
