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
public class IndexDelta extends IntradayValue {
    @Builder
    public IndexDelta(Long number, LocalDateTime dateTime, String ticker, Double price, Double value) {
        super(number, dateTime, ticker, price, value);
    }
}
