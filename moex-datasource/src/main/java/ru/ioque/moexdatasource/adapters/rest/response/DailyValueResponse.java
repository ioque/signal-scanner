package ru.ioque.moexdatasource.adapters.rest.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DailyValueResponse {
    String ticker;
    Double openPrice;
    Double closePrice;
    Double highPrice;
    Double lowPrice;
    Double waPrice;
}
