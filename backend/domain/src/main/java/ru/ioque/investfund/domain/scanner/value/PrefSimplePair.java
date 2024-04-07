package ru.ioque.investfund.domain.scanner.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PrefSimplePair {
    TradingSnapshot pref;
    TradingSnapshot simple;

    public Double getCurrentDelta() {
        var simplePrice = simple.getTodayLastPrice();
        var prefPrice = pref.getTodayLastPrice();
        if (simplePrice.isEmpty() || prefPrice.isEmpty()) return 0D;
        return simplePrice.get() - prefPrice.get();
    }

    public Double getHistoryDelta() {
        if (simple.getWaPriceSeries().isEmpty()) return 0D;
        if (pref.getWaPriceSeries().isEmpty()) return 0D;
        final List<Double> historyDelta = simple
            .getWaPriceSeries()
            .stream()
            .map(row -> row.getValue() - getWaPricePrefBy(row.getTime()))
            .sorted()
            .toList();
        final int n = historyDelta.size();
        if (n % 2 != 0)
            return historyDelta.get(n / 2);
        return (historyDelta.get((n - 1) / 2) + historyDelta.get(n / 2)) / 2.0;
    }

    private Double getWaPricePrefBy(ChronoLocalDate time) {
        return pref.getWaPriceSeries()
            .stream()
            .filter(pref -> pref.getTime().equals(time))
            .findFirst()
            .orElseThrow()
            .getValue();
    }
}
