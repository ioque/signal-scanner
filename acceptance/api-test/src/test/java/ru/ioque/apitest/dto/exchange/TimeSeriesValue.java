package ru.ioque.apitest.dto.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeSeriesValue<V extends Number, T> {
    V value;
    T time;
}
