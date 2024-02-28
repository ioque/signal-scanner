package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class InstrumentValue implements DatasetObject {
    protected InstrumentType type;
    protected StringValue secId;
    public boolean equalsBy(InstrumentType type) {
        return this.type.equals(type);
    }
    public boolean equalsBy(String secId) {
        return Objects.equals(this.secId.getValue(), secId);
    }
}
