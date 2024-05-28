package ru.ioque.investfund.domain.scanner.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.TradingState;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class PrefSimplePair {
    Instrument pref;
    Instrument simple;

    public Double getCurrentDelta() {
        if (simple.getTradingState().isEmpty() || pref.getTradingState().isEmpty()) {
            return 0D;
        }
        final TradingState simpleState = simple.getTradingState().get();
        final TradingState prefState = pref.getTradingState().get();
        if (simpleState.getTodayLastPrice() == null || prefState.getTodayLastPrice() == null) {
            return 0D;
        }
        return simpleState.getTodayLastPrice() - prefState.getTodayLastPrice();
    }

    public Double getHistoryDelta() {
        if (simple.getAggregateHistories().isEmpty()) return 0D;
        if (pref.getAggregateHistories().isEmpty()) return 0D;
        final List<Double> historyDelta = simple
            .getAggregateHistories()
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
        return pref.getAggregateHistories()
            .stream()
            .filter(pref -> pref.getDate().equals(time))
            .findFirst()
            .orElseThrow()
            .getWaPrice();
    }
}
