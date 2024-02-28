package ru.ioque.acceptance.application.tradingdatagenerator.stock;

import ru.ioque.acceptance.application.tradingdatagenerator.PercentageGrowths;
import ru.ioque.acceptance.application.tradingdatagenerator.StockHistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.AbstractValueGenerator;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockDailyResultGenerator extends AbstractValueGenerator {
    public List<StockDailyResult> generateStockHistory(StockHistoryGeneratorConfig config) {
        List<StockDailyResult> stockDailyResults = new ArrayList<>();
        for (int i = 0; i < config.getValuePercentageGrowths().size(); i++) {
            Double startClose = getStartClose(config, stockDailyResults);
            Double startOpen = getStartOpen(config, stockDailyResults);
            Double startValue = getStartValue(config, stockDailyResults);
            LocalDate startDate = getStartDate(config, stockDailyResults);
            int days = (int) (config.getDays() * config.getValuePercentageGrowths().get(0).getWeight());
            stockDailyResults.addAll(generateStockDailyResultBatch(
                config.getOpenPricePercentageGrowths().get(i),
                config.getClosePricePercentageGrowths().get(i),
                config.getValuePercentageGrowths().get(i),
                config.getTicker(),
                startClose,
                startOpen,
                startValue,
                days,
                startDate
            ));
        }
        return stockDailyResults;
    }

    private LocalDate getStartDate(StockHistoryGeneratorConfig config, List<StockDailyResult> stockDailyResults) {
        return stockDailyResults.isEmpty() ? config.getStartDate() : (LocalDate) stockDailyResults.get(
            stockDailyResults.size() - 1).getTradeDate().getValue();
    }

    private double getStartValue(StockHistoryGeneratorConfig config, List<StockDailyResult> stockDailyResults) {
        return stockDailyResults.isEmpty() ? config.getStartValue() : (double) stockDailyResults.get(
            stockDailyResults.size() - 1).getValue().getValue();
    }

    private double getStartOpen(StockHistoryGeneratorConfig config, List<StockDailyResult> stockDailyResults) {
        return stockDailyResults.isEmpty() ? config.getStartOpen() : (double) stockDailyResults.get(
            stockDailyResults.size() - 1).getOpen().getValue();
    }

    private double getStartClose(StockHistoryGeneratorConfig config, List<StockDailyResult> stockDailyResults) {
        return stockDailyResults.isEmpty() ? config.getStartClose() : (double) stockDailyResults.get(
            stockDailyResults.size() - 1).getClose().getValue();
    }

    private List<StockDailyResult> generateStockDailyResultBatch(
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

        List<StockDailyResult> dailyResults = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            double open = startOpen + deltaOpen * i;
            double close = startClose + deltaClose * i;
            double value = startValue + deltaValue * i;
            double volume = (long) value / ((open + close) / 2);
            if (isWeekend(date)) {
                continue;
            }
            dailyResults.add(
                StockDailyResult.builder()
                    .secId(ticker)
                    .tradeDate(date)
                    .open(open)
                    .close(close)
                    .value(value)
                    .volume(volume)
                    .build()
            );
        }

        return dailyResults;
    }
}
