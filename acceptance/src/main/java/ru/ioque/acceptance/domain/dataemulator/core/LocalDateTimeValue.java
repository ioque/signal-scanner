package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalDateTimeValue extends DatasetValue {
    LocalDateTime value;

    @Builder
    public LocalDateTimeValue(String columnName, Integer order, LocalDateTime value) {
        super(columnName, order);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
