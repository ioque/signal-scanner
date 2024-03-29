package ru.ioque.core.dataemulator.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalDateValue extends DatasetValue {
    LocalDate value;

    @Builder
    public LocalDateValue(String columnName, Integer order, LocalDate value) {
        super(columnName, order);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
