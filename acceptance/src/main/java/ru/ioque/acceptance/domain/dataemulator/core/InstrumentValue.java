package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class InstrumentValue implements DatasetObject {
    protected InstrumentType type;
    protected StringValue secId;
    @Getter
    protected List<? extends IntradayValue> intradayValues;
    @Getter
    protected List<? extends HistoryValue> historyValues;
    public boolean equalsBy(InstrumentType type) {
        return this.type.equals(type);
    }
    public boolean equalsBy(String secId) {
        return Objects.equals(this.secId.getValue(), secId);
    }
}
