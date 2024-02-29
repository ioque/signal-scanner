package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class DailyResultValue implements DatasetObject {
    StringValue secId;
    LocalDateValue tradeDate;
    StringValue boardId;
    DoubleValue open;
    DoubleValue low;
    DoubleValue high;
    DoubleValue close;

    public abstract DoubleValue getValue();

    public boolean equalsBy(String secId) {
        return Objects.equals(this.secId.getValue(), secId);
    }
    public boolean isBetween(LocalDate from, LocalDate till) {
        var tradeDate = ((LocalDate) this.tradeDate.getValue());
        return (tradeDate.isBefore(till) || tradeDate.isEqual(till)) && (tradeDate.isAfter(from) || tradeDate.isEqual(from));
    }
}
