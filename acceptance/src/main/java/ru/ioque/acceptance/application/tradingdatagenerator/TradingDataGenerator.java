package ru.ioque.acceptance.application.tradingdatagenerator;

import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;

import java.util.ArrayList;
import java.util.List;

public class TradingDataGenerator {
    public List<StockDailyResult> generateStockHistory(StockHistoryGeneratorConfig config) {
        if (config.getClosePricePercentageGrowths().size() == 1 &&
            config.getOpenPricePercentageGrowths().size() == 1 &&
            config.getValuePercentageGrowths().size() == 1) {

            Double startClose = config.getStartClose();
            Double startOpen = config.getStartOpen();
            Double startValue = config.getStartValue();
            int days = config.getDays();
            double finalOpen = startOpen + config.getOpenPricePercentageGrowths().get(0).value * startOpen / 100;
            double deltaOpen = (finalOpen - startOpen) / (days - 1);

            double finalClose = startClose + config.getOpenPricePercentageGrowths().get(0).value * startClose / 100;
            double deltaClose = (finalClose - startClose) / (days - 1);

            double finalValue = startValue + config.getOpenPricePercentageGrowths().get(0).value * startValue / 100;
            double deltaValue = (finalValue - startValue) / (days - 1);

            List<StockDailyResult> dailyResults = new ArrayList<>(
                List.of(
                    StockDailyResult.builder()
                        .secId(config.getTicker())
                        .tradeDate(config.getStartDate())
                        .open(startOpen)
                        .close(startClose)
                        .value(startValue)
                        .build()
                )
            );

            for (int i = 1; i < days; i++) {
                double open = startOpen + deltaOpen * i;
                double close = startClose + deltaClose * i;
                double value = startValue + deltaValue * i;
                double volume = (long) value / ((open + close) / 2);
                dailyResults.add(
                    StockDailyResult.builder()
                        .secId(config.getTicker())
                        .tradeDate(config.getStartDate().plusDays(1))
                        .open(open)
                        .close(close)
                        .value(value)
                        .volume(volume)
                        .build()
                );
            }

            return dailyResults;
        }

        return List.of();
    }
}
