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
public class Index extends Instrument {
    Double annualHigh;
    Double annualLow;

    @Builder
    public Index(
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Double annualHigh,
        Double annualLow,
        List<IntradayValue> intradayValues,
        List<DailyValue> dailyValues
    ) {
        super(id, ticker, shortName, name, updatable, intradayValues, dailyValues);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }
}
