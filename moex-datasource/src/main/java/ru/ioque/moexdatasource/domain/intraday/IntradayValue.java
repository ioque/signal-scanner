package ru.ioque.moexdatasource.domain.intraday;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class IntradayValue implements Serializable {
    String ticker;
    Long number;
    String board;
    LocalDateTime dateTime;
    Double value;
    Double price;

    public IntradayValue(
        String ticker,
        Long number,
        String board,
        LocalDateTime dateTime,
        Double value,
        Double price
    ) {
        this.ticker = ticker;
        this.number = number;
        this.board = board;
        this.dateTime = dateTime;
        this.value = value;
        this.price = price;
    }
}
