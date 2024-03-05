package ru.ioque.investfund.domain.exchange.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.statistic.TimeSeriesValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.Deal;
import ru.ioque.investfund.domain.exchange.value.tradingData.DealResult;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.time.chrono.ChronoLocalDate;
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
            .filter(row -> row.getIsBuy().equals(Boolean.FALSE))
            .mapToDouble(Deal::getValue)
            .sum();
        return buyValue / sellValue;
    }
}
