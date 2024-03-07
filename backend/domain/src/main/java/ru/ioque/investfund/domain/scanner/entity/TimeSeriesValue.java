package ru.ioque.investfund.domain.scanner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.temporal.Temporal;

@Getter
@AllArgsConstructor
public class TimeSeriesValue<V extends Number, T extends Temporal & Comparable<T>> implements Comparable<TimeSeriesValue<V, T>> {
    V value;
    T time;

    @Override
    public int compareTo(TimeSeriesValue<V, T> vtTimeSeriesValue) {
        return time.compareTo(vtTimeSeriesValue.getTime());
    }
}
