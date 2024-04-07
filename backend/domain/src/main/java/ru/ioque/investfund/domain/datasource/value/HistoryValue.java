package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValue implements Comparable<HistoryValue>, Serializable {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double lowPrice;
    Double highPrice;
    Double waPrice;
    Double value;

    @Override
    public int compareTo(HistoryValue historyValue) {
        return Objects.compare(getTradeDate(), historyValue.getTradeDate(), LocalDate::compareTo);
    }

    public boolean isAfter(HistoryValue historyValue) {
        return getTradeDate().isAfter(historyValue.getTradeDate());
    }
}
