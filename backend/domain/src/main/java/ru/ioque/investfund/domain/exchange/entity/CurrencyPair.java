package ru.ioque.investfund.domain.exchange.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.statistic.TimeSeriesValue;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.Deal;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrencyPair extends Instrument {
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPair(
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotSize,
        String faceUnit,
        List<IntradayValue> intradayValues,
        List<DailyValue> dailyValues
    ) {
        super(id, ticker, shortName, name, updatable, intradayValues, dailyValues);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    @Override
    public List<TimeSeriesValue<Double, ChronoLocalDate>> getWaPriceSeries() {
        return getDailyValues()
            .stream()
            .map(DealResult.class::cast)
            .map(row -> new TimeSeriesValue<>(row.getWaPrice(), row.getTradeDate()))
            .toList();
    }

    @Override
    public Double getBuyToSellValueRatio() {
        Double buyValue = getIntradayValues()
            .stream()
            .map(Deal.class::cast)
            .filter(row -> row.getIsBuy().equals(Boolean.TRUE))
            .mapToDouble(Deal::getValue)
            .sum();
        Double sellValue = getIntradayValues()
            .stream()
            .map(Deal.class::cast)
            .filter(row -> row.getIsBuy().equals(Boolean.TRUE))
            .mapToDouble(Deal::getValue)
            .sum();
        return buyValue / sellValue;
    }
}
