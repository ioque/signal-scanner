package ru.ioque.acceptance.application.tradingdatagenerator;

import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public List<StockTrade> generateStockTrades(StockTradesGeneratorConfig config) {
        if (config.getValuePercentageGrowths().size() == 1 && config.getPricePercentageGrowths().size() == 1) {
            String ticker = config.getTicker();
            Double startValue = config.getStartValue();
            Double startPrice = config.getStartPrice();
            long numTrades = config.getNumTrades();
            LocalDate nowDate = config.getDate();
            LocalTime startTime = config.getStartTime();
            String buysell = config.getPricePercentageGrowths().get(0).value >= 0 ? "BUY" : "SELL";
            double finalPrice = startPrice + config.getPricePercentageGrowths().get(0).value * startPrice / 100;
            double deltaPrice = (finalPrice - startPrice) / (numTrades - 1);

            double finalValue = startValue + config.getValuePercentageGrowths().get(0).value * startValue / 100;
            double deltaValue = (finalValue - startValue) / (numTrades - 1);

            List<StockTrade> stockTrades = new ArrayList<>(
                List.of(
                    StockTrade.builder()
                        .secId(ticker)
                        .tradeNo(1)
                        .tradeTime(startTime)
                        .value(startValue)
                        .price(startPrice)
                        .buySell(buysell)
                        .sysTime(nowDate.atTime(startTime))
                        .build()
                )
            );

            for (int i = 2; i <= numTrades; i++) {
                stockTrades.add(
                    StockTrade.builder()
                        .secId(ticker)
                        .tradeNo(i)
                        .tradeTime(startTime.plusSeconds(i))
                        .value(startValue + deltaValue * i)
                        .price(startPrice + deltaPrice * i)
                        .buySell(buysell)
                        .sysTime(nowDate.atTime(startTime.plusSeconds(i)))
                        .build()
                );
            }

            return stockTrades;
        }
        return List.of();
    }
}
