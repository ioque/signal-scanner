package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class DailyResultValue implements DatasetObject {
    protected StringValue secId;
    @Getter
    protected LocalDateValue tradeDate;
    public boolean equalsBy(String secId) {
        return Objects.equals(this.secId.getValue(), secId);
    }
    public boolean isBetween(LocalDate from, LocalDate till) {
        var tradeDate = ((LocalDate) this.tradeDate.getValue());
        return (tradeDate.isBefore(till) || tradeDate.isEqual(till)) && (tradeDate.isAfter(from) || tradeDate.isEqual(from));
    }
}
