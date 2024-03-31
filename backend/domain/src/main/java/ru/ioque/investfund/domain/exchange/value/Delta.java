package ru.ioque.investfund.domain.exchange.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Delta extends IntradayValue {
    @Builder
    public Delta(LocalDateTime dateTime, String ticker, Double price, Double value) {
        super(dateTime, ticker, price, value);
    }
}
