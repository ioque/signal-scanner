package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalTimeValue extends DatasetValue {
    LocalTime value;

    @Builder
    public LocalTimeValue(String columnName, Integer order, LocalTime value) {
        super(columnName, order);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
