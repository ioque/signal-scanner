package ru.ioque.core.datagenerator.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryValue implements Comparable<HistoryValue> {
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

    @Override
    public int compareTo(HistoryValue historyValue) {
        return tradeDate.compareTo(historyValue.tradeDate);
    }
}
