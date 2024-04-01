package ru.ioque.core.datagenerator.core;

import ru.ioque.core.datagenerator.history.HistoryValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistoryGenerator extends AbstractGenerator {
    public List<HistoryValue> generateHistory(HistoryGeneratorConfig config) {
        List<HistoryValue> history = new ArrayList<>();
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

    private LocalDate getStartDate(HistoryGeneratorConfig config, List<HistoryValue> history) {
        return history.isEmpty() ?
            config.getStartDate() :
            history.get(history.size() - 1).getTradeDate();
    }

    private double getStartValue(HistoryGeneratorConfig config, List<HistoryValue> history) {
        return history.isEmpty() ?
            config.getTradeValue().getStartValue() :
            history.get(history.size() - 1).getValue();
    }

    private double getStartOpen(HistoryGeneratorConfig config, List<HistoryValue> history) {
        return history.isEmpty() ?
            config.getOpenPrice().getStartValue() :
            history.get(history.size() - 1).getOpenPrice();
    }

    private double getStartClose(HistoryGeneratorConfig config, List<HistoryValue> history) {
        return history.isEmpty() ?
            config.getClosePrice().getStartValue() :
            history.get(history.size() - 1).getClosePrice();
    }

    public List<HistoryValue> generateHistoryBatch(
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

        List<HistoryValue> dailyResults = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            double open = startOpen + deltaOpen * i;
            double close = startClose + deltaClose * i;
            double value = startValue + deltaValue * i;
            if (isWeekend(date)) {
                continue;
            }
            dailyResults.add(buildHistoryValue(ticker, date, open, close, value));
        }

        return dailyResults;
    }

    public HistoryValue buildHistoryValue(
        String ticker,
        LocalDate date,
        double open,
        double close,
        double value
    ) {
        return HistoryValue.builder()
            .ticker(ticker)
            .tradeDate(date)
            .openPrice(open)
            .closePrice(close)
            .value(value)
            .build();
    }
}
