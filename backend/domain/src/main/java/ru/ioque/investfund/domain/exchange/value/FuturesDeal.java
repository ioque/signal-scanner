package ru.ioque.investfund.domain.exchange.value;

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
    public FuturesDeal(Long number, LocalDateTime dateTime, String ticker, Double price, Double value, Integer qnt) {
        super(number, dateTime, ticker, price, value);
        this.qnt = qnt;
    }
}
