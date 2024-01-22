package ru.ioque.investfund.domain.exchange.value.tradingData;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FuturesDeal extends IntradayValue {
    Integer qnt;

    @Builder
    public FuturesDeal(Long number, LocalDateTime dateTime, String ticker, Double price, Integer qnt) {
        super(number, dateTime, ticker, price);
        this.qnt = qnt;
    }
    @Override
    public double getValue() {
        return qnt * getPrice();
    }
}
