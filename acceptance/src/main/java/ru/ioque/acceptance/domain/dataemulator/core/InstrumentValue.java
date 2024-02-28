package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class InstrumentValue implements DatasetObject {
    protected InstrumentType type;
    protected StringValue secId;
    public boolean equalsBy(InstrumentType type) {
        return this.type.equals(type);
    }
}
