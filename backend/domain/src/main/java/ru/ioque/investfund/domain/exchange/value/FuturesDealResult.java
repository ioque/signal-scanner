package ru.ioque.investfund.domain.exchange.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FuturesDealResult extends DailyValue {
    Double openPositionValue;
    Integer volume;

    @Builder
    public FuturesDealResult(
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double value,
        Double openPositionValue,
        Integer volume
    ) {
        super(tradeDate, ticker, openPrice, closePrice, minPrice, maxPrice, value);
        this.openPositionValue = openPositionValue;
        this.volume = volume;
    }
}
