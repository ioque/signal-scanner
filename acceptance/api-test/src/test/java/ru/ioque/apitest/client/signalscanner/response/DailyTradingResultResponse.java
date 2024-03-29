package ru.ioque.apitest.client.signalscanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DailyTradingResultResponse implements Serializable {
    LocalDate tradeDate;
    String ticker;
    Double volume;
    Double waPrice;
    Double openPrice;
    Double closePrice;
}
