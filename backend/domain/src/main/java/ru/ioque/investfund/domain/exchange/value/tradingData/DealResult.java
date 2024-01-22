package ru.ioque.investfund.domain.exchange.value.tradingData;

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
public class DealResult extends DailyValue {
    Double numTrades;
    Double waPrice;
    Double volume;

    @Builder
    public DealResult(
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double value,
        Double numTrades,
        Double waPrice,
        Double volume
    ) {
        super(tradeDate, ticker, openPrice, closePrice, minPrice, maxPrice, value);
        this.numTrades = numTrades;
        this.waPrice = waPrice;
        this.volume = volume;
    }
}
