package ru.ioque.moexdatasource.domain.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Delta extends IntradayValue {
    @Builder
    public Delta(
        String ticker,
        Long tradeNumber,
        String board,
        LocalDateTime dateTime,
        Double value,
        Double price
    ) {
        super(ticker, tradeNumber, board, dateTime, value, price);
    }
}
