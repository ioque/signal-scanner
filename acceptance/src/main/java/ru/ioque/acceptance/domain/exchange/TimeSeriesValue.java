package ru.ioque.acceptance.domain.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeSeriesValue<V extends Number, T> {
    V value;
    T time;
}
