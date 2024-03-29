package ru.ioque.core.dataemulator.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class DatasetValue implements Comparable<DatasetValue> {
    String columnName;
    Integer order;

    @Override
    public int compareTo(DatasetValue datasetValue) {
        return this.order.compareTo(datasetValue.order);
    }

    public abstract Object getValue();
}
