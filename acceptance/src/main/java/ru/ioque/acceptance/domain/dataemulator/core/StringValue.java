package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StringValue extends DatasetValue {
    String value;

    @Builder
    public StringValue(String columnName, Integer order, String value) {
        super(columnName, order);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
