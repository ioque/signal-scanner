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
import java.util.UUID;

@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValue implements Comparable<HistoryValue>, Serializable {
    UUID datasourceId;
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
}
