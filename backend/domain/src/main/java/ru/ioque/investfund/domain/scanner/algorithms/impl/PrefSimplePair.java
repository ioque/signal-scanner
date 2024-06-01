package ru.ioque.investfund.domain.scanner.algorithms.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.ioque.investfund.domain.scanner.value.InstrumentTradingState;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PrefSimplePair {
    InstrumentTradingState pref;
    InstrumentTradingState simple;

    public Double getCurrentDelta() {
        final IntradayPerformance simpleState = simple.getIntradayPerformance();
        final IntradayPerformance prefState = pref.getIntradayPerformance();
        if (simpleState.getTodayLastPrice() == 0 || prefState.getTodayLastPrice() == 0) {
            return 0D;
        }
        return simpleState.getTodayLastPrice() - prefState.getTodayLastPrice();
    }

    public Double getHistoryDelta() {
        if (simple.getAggregatedHistories().isEmpty()) return 0D;
        if (pref.getAggregatedHistories().isEmpty()) return 0D;
        final List<Double> historyDelta = simple
            .getAggregatedHistories()
            .stream()
            .map(row -> row.getWaPrice() - getWaPricePrefBy(row.getDate()))
            .sorted()
            .toList();
        final int n = historyDelta.size();
        if (n % 2 != 0)
            return historyDelta.get(n / 2);
        return (historyDelta.get((n - 1) / 2) + historyDelta.get(n / 2)) / 2.0;
    }

    private Double getWaPricePrefBy(ChronoLocalDate time) {
        return pref.getAggregatedHistories()
            .stream()
            .filter(pref -> pref.getDate().equals(time))
            .findFirst()
            .orElseThrow()
            .getWaPrice();
    }
}
