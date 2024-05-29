package ru.ioque.investfund.domain.scanner.value;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PrefSimplePair {
    InstrumentPerformance pref;
    InstrumentPerformance simple;

    public Double getCurrentDelta() {
        if (simple.getIntradayPerformance().isEmpty() || pref.getIntradayPerformance().isEmpty()) {
            return 0D;
        }
        final IntradayPerformance simpleState = simple.getIntradayPerformance().get();
        final IntradayPerformance prefState = pref.getIntradayPerformance().get();
        if (simpleState.getTodayLastPrice() == null || prefState.getTodayLastPrice() == null) {
            return 0D;
        }
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
