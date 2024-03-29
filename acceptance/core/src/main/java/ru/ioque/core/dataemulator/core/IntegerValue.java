package ru.ioque.core.dataemulator.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IntegerValue extends DatasetValue {
    Integer value;

    @Builder
    public IntegerValue(String columnName, Integer order, Integer value) {
        super(columnName, order);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
