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
public class IndexDelta extends IntradayValue {
    Double value;

    @Builder
    public IndexDelta(Long number, LocalDateTime dateTime, String ticker, Double price, Double value) {
        super(number, dateTime, ticker, price);
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }
}
