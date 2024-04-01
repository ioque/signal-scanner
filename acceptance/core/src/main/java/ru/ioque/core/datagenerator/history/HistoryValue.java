package ru.ioque.core.datagenerator.history;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValue {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double highPrice;
    Double lowPrice;
    Double waPrice;
    Double value;

    public boolean isBetween(LocalDate from, LocalDate till) {
        return (tradeDate.isAfter(from) || tradeDate.isEqual(from)) && (tradeDate.isBefore(till) || tradeDate.isEqual(till));
    }
}
