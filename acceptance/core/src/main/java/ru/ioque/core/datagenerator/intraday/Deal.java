package ru.ioque.core.datagenerator.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Deal extends IntradayValue {
    Integer qnt;
    Boolean isBuy;

    @Builder
    public Deal(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double value,
        Double price,
        Integer qnt,
        Boolean isBuy
    ) {
        super(number, dateTime, ticker, value, price);
        this.qnt = qnt;
        this.isBuy = isBuy;
    }
}
