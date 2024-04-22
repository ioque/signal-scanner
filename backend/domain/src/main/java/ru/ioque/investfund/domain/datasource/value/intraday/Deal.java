package ru.ioque.investfund.domain.datasource.value.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Deal extends IntradayData {
    Boolean isBuy;
    Integer qnt;

    @Builder
    public Deal(
        Ticker ticker,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Boolean isBuy,
        Integer qnt,
        Double value
    ) {
        super(ticker, number, dateTime, price, value);
        this.isBuy = isBuy;
        this.qnt = qnt;
    }
}