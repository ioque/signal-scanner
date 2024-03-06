package ru.ioque.investfund.domain.statistic;

import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.Deal;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

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
            .todayLastPrice(instrument.getLastDealPrice().orElse(0.0))
            .todayOpenPrice(instrument.getFirstDealPrice())
            .todayValue(calcTodayValue(instrument.getIntradayValues()))
            .buyToSellValuesRatio(calcBuyToSellValueRatio(instrument))
            .historyMedianValue(calcMedianValue(instrument.getDailyValues()))
            .build();
    }

    private Double calcTodayValue(Set<IntradayValue> intradayValues) {
        return intradayValues
            .stream()
            .mapToDouble(IntradayValue::getValue)
            .sum();
    }

    private Double calcBuyToSellValueRatio(Instrument instrument) {
        double buyValue = instrument.getIntradayValues()
            .stream()
            .filter(row -> row.getClass().equals(Deal.class))
            .map(Deal.class::cast)
            .filter(row -> row.getIsBuy().equals(Boolean.TRUE))
            .mapToDouble(Deal::getValue)
            .sum();
        double sellValue = instrument.getIntradayValues()
            .stream()
            .filter(row -> row.getClass().equals(Deal.class))
            .map(Deal.class::cast)
            .filter(row -> row.getIsBuy().equals(Boolean.FALSE))
            .mapToDouble(Deal::getValue)
            .sum();
        if (buyValue == 0 || sellValue == 0) return 1D;
        return buyValue / sellValue;
    }
}
