package ru.ioque.investfund.domain.datasource.value;

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
public class AggregatedHistory implements Comparable<AggregatedHistory> {
    Long id;
    LocalDate date;
    Double lowPrice;
    Double highPrice;
    Double openPrice;
    Double closePrice;
    Double waPrice;
    Double value;

    @Override
    public int compareTo(AggregatedHistory aggregatedHistory) {
        return date.compareTo(aggregatedHistory.date);
    }
}
