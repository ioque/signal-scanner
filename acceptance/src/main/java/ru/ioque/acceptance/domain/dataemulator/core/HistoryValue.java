package ru.ioque.acceptance.domain.dataemulator.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class HistoryValue implements DatasetObject {
    protected LocalDateValue tradeDate;
    public boolean isBetween(LocalDate from, LocalDate till) {
        var tradeDate = ((LocalDate) this.tradeDate.getValue());
        return (tradeDate.isBefore(till) || tradeDate.isEqual(till)) && (tradeDate.isAfter(from) || tradeDate.isEqual(from));
    }
}
