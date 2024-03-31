package ru.ioque.moexdatasource.domain.history;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HistoryValue implements Serializable {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double minPrice;
    Double maxPrice;
    Double waPrice;
    Double value;

    @Builder
    public HistoryValue(
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double waPrice,
        Double value
    ) {
        this.tradeDate = tradeDate;
        this.ticker = ticker;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.waPrice = waPrice;
        this.value = value;
    }
}
