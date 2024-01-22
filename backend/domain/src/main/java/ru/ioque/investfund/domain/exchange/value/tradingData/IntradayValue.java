package ru.ioque.investfund.domain.exchange.value.tradingData;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class IntradayValue implements Serializable, Comparable<IntradayValue> {
    Long number;
    LocalDateTime dateTime;
    String ticker;
    Double price;

    public IntradayValue(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price
    ) {
        this.number = number;
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.price = price;
    }

    public boolean isAfter(IntradayValue intradayValue) {
        return getDateTime().isAfter(intradayValue.getDateTime());
    }

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return Objects.compare(getDateTime(), intradayValue.getDateTime(), LocalDateTime::compareTo);
    }

    public abstract double getValue();
}
