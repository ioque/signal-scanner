package ru.ioque.moexdatasource.domain.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Deal extends IntradayValue {
    Integer qnt;
    Boolean isBuy;

    @Builder
    public Deal(
        String ticker,
        Long number,
        String board,
        LocalDateTime dateTime,
        Double value,
        Double price,
        Integer qnt,
        Boolean isBuy
    ) {
        super(ticker, number, board, dateTime, value, price);
        this.qnt = qnt;
        this.isBuy = isBuy;
    }
}
