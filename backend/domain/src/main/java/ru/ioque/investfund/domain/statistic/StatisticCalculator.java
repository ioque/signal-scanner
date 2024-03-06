package ru.ioque.investfund.domain.statistic;

import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DailyValue;

import java.util.Set;

public class StatisticCalculator {
    public Double calcMedianValue(Set<DailyValue> dailyValues) {
        var sortedValues = dailyValues.stream().sorted().toList();
        var n = sortedValues.size();
        if (n % 2 != 0)
            return sortedValues.get(n / 2).getValue();
        return (sortedValues.get((n - 1) / 2).getValue() + sortedValues.get(n / 2).getValue()) / 2.0;
    }

    public InstrumentStatistic calcStatistic(Instrument instrument) {
        return InstrumentStatistic
            .builder()
            .instrumentId(instrument.getId())
            .ticker(instrument.getTicker())
            .todayLastPrice(instrument.getLastDealPrice().orElse(0.0))
            .todayOpenPrice(instrument.getFirstDealPrice())
            .todayValue(instrument.getTodayValue())
            .buyToSellValuesRatio(instrument.getBuyToSellValueRatio())
            .historyMedianValue(calcMedianValue(instrument.getDailyValues()))
            .closePriceSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate())).toList())
            .openPriceSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate())).toList())
            .openPriceSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate())).toList())
            .valueSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate())).toList())
            .waPriceSeries(instrument.getWaPriceSeries())
            .todayPriceSeries(instrument.getIntradayValues().stream().map(intradayValue -> new TimeSeriesValue<>(intradayValue.getPrice(), intradayValue.getDateTime().toLocalTime())).toList())
            .build();
    }
}
