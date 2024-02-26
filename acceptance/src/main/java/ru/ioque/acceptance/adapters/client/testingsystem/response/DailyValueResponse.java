package ru.ioque.acceptance.adapters.client.testingsystem.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyValueResponse {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double minPrice;
    Double maxPrice;
    Double value;
}
