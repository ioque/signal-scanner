package ru.ioque.investfund.domain.scanner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PrefSimplePair {
    FinInstrument pref;
    FinInstrument simple;

    public Double getCurrentDelta() {
        return simple.getTodayLastPrice() - pref.getTodayLastPrice();
    }

    public Double getHistoryDelta() {
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
