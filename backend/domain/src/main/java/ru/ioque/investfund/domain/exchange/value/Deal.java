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
public class Deal extends IntradayValue {
    Boolean isBuy;
    Integer qnt;
    Double value;

    @Builder
    public Deal(Long number, LocalDateTime dateTime, String ticker, Double price, Boolean isBuy, Integer qnt, Double value) {
        super(number, dateTime, ticker, price);
        this.isBuy = isBuy;
        this.qnt = qnt;
        this.value = value;
    }

    @Override
    public Double getPrice() {
        return super.getPrice() * (isBuy.equals(Boolean.TRUE) ? 1 : -1);
    }
    @Override
    public double getValue() {
        return value;
    }
}
