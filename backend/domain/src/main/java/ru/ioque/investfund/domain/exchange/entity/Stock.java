package ru.ioque.investfund.domain.exchange.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.util.List;
import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Stock extends Instrument {
    Integer lotSize;
    String isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public Stock(
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotSize,
        String isin,
        String regNumber,
        Integer listLevel,
        List<IntradayValue> intradayValues,
        List<DailyValue> dailyValues
    ) {
        super(id, ticker, shortName, name, updatable, intradayValues, dailyValues);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }
}
