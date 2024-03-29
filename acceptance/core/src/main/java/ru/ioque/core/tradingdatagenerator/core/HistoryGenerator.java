package ru.ioque.core.tradingdatagenerator.core;

import ru.ioque.core.dataemulator.core.DailyResultValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class HistoryGenerator<T extends DailyResultValue> extends AbstractGenerator<T> {
    public List<T> generateHistory(HistoryGeneratorConfig config) {
        List<T> history = new ArrayList<>();
        for (int i = 0; i < config.getTradeValue().getPercentageGrowths().size(); i++) {
            Double startClose = getStartClose(config, history);
            Double startOpen = getStartOpen(config, history);
            Double startValue = getStartValue(config, history);
            LocalDate startDate = getStartDate(config, history);
            int days = (int) (config.getDays() * config.getTradeValue().getPercentageGrowths().get(i).getWeight());
            history.addAll(generateHistoryBatch(
                config.getOpenPrice().getPercentageGrowths().get(i),
                config.getClosePrice().getPercentageGrowths().get(i),
                config.getTradeValue().getPercentageGrowths().get(i),
                config.getTicker(),
                startClose,
                startOpen,
                startValue,
                days,
                startDate
            ));
        }
        return history;
    }

    private LocalDate getStartDate(HistoryGeneratorConfig config, List<T> history) {
        return history.isEmpty() ? config.getStartDate() : (LocalDate) history.get(
            history.size() - 1).getTradeDate().getValue();
    }

    private double getStartValue(HistoryGeneratorConfig config, List<T> history) {
        return history.isEmpty() ? config.getTradeValue().getStartValue() : (double) history.get(
            history.size() - 1).getValue().getValue();
    }

    private double getStartOpen(HistoryGeneratorConfig config, List<T> history) {
        return history.isEmpty() ? config.getOpenPrice().getStartValue() : (double) history.get(
            history.size() - 1).getOpen().getValue();
    }

    private double getStartClose(HistoryGeneratorConfig config, List<T> history) {
        return history.isEmpty() ? config.getClosePrice().getStartValue() : (double) history.get(
            history.size() - 1).getClose().getValue();
    }

    public List<T> generateHistoryBatch(
        PercentageGrowths openPricePercentageGrowths,
        PercentageGrowths closePricePercentageGrowths,
        PercentageGrowths valuePercentageGrowths,
        String ticker,
        Double startClose,
        Double startOpen,
        Double startValue,
        int days,
        LocalDate startDate
    ) {
        double finalOpen = linearGrowthFinalResult(openPricePercentageGrowths.getValue(), startOpen);
        double deltaOpen = getDeltaByMean(startOpen, finalOpen, days);

        double finalClose = linearGrowthFinalResult(closePricePercentageGrowths.getValue(), startClose);
        double deltaClose = getDeltaByMean(startClose, finalClose, days);

        double finalValue = linearGrowthFinalResult(valuePercentageGrowths.getValue(), startValue);
        double deltaValue = getDeltaByMean(startValue, finalValue, days);

        List<T> dailyResults = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            double open = startOpen + deltaOpen * i;
            double close = startClose + deltaClose * i;
            double value = startValue + deltaValue * i;
            double volume = (long) value / ((open + close) / 2);
            if (isWeekend(date)) {
                continue;
            }
            dailyResults.add(buildHistoryValue(ticker, date, open, close, value, volume));
        }

        return dailyResults;
    }

    public abstract T buildHistoryValue(
        String ticker,
        LocalDate date,
        double open,
        double close,
        double value,
        double volume
    );
}
