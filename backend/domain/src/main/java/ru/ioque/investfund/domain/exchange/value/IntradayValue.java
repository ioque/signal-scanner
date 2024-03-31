package ru.ioque.investfund.domain.exchange.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
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
    Double value;

    public IntradayValue(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        this.number = number;
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.price = price;
        this.value = value;
    }

    public boolean isSameByDate(LocalDate date) {
        return getDateTime().toLocalDate().isEqual(date);
    }

    public boolean isAfter(IntradayValue intradayValue) {
        return getDateTime().isAfter(intradayValue.getDateTime());
    }

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return Objects.compare(getDateTime(), intradayValue.getDateTime(), LocalDateTime::compareTo);
    }
}
