package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AggregateHistory implements Comparable<AggregateHistory> {
    LocalDate date;
    Double lowPrice;
    Double highPrice;
    Double openPrice;
    Double closePrice;
    Double waPrice;
    Double value;

    public static AggregateHistory from(HistoryValue value) {
        return AggregateHistory.builder()
            .date(value.getTradeDate())
            .lowPrice(value.getLowPrice())
            .highPrice(value.getHighPrice())
            .openPrice(value.getOpenPrice())
            .closePrice(value.getClosePrice())
            .waPrice(value.getWaPrice())
            .value(value.getValue())
            .build();
    }

    @Override
    public int compareTo(AggregateHistory aggregateHistory) {
        return date.compareTo(aggregateHistory.date);
    }
}
