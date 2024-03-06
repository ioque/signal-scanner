package ru.ioque.investfund.domain.exchange.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Агрегированные итоги торгов на дату
 */

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class DailyValue implements Comparable<DailyValue>, Serializable {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double minPrice;
    Double maxPrice;
    Double value;

    public DailyValue(
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double value
    ) {
        this.tradeDate = tradeDate;
        this.ticker = ticker;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.value = value;
    }

    @Override
    public int compareTo(DailyValue dailyValue) {
        return Objects.compare(getTradeDate(), dailyValue.getTradeDate(), LocalDate::compareTo);
    }

    public boolean isAfter(DailyValue dailyValue) {
        return getTradeDate().isAfter(dailyValue.getTradeDate());
    }
}
