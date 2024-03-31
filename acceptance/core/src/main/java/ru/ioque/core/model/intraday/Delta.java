package ru.ioque.core.model.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Delta extends IntradayValue {

    @Builder
    public Delta(Long tradeNumber, LocalDateTime dateTime, String ticker, Double value, Double price) {
        super(tradeNumber, dateTime, ticker, value, price);
    }
}
