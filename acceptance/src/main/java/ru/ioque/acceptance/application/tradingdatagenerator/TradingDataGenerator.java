package ru.ioque.acceptance.application.tradingdatagenerator;

import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TradingDataGenerator {
    public List<StockDailyResult> generateStockHistory(StockHistoryGeneratorConfig config) {
        List<StockDailyResult> stockDailyResults = new ArrayList<>();
        for (int i = 0; i < config.getValuePercentageGrowths().size(); i++) {
            Double startClose = getStartClose(config, stockDailyResults);
            Double startOpen = getStartOpen(config, stockDailyResults);
            Double startValue = getStartValue(config, stockDailyResults);
            LocalDate startDate = getStartDate(config, stockDailyResults);
            int days = (int) (config.getDays() * config.getValuePercentageGrowths().get(0).weight);
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

    private static List<StockDailyResult> generateStockDailyResultBatch(
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
        double finalOpen = startOpen + openPricePercentageGrowths.value * startOpen / 100;
        double deltaOpen = (finalOpen - startOpen) / (days - 1);

        double finalClose = startClose + closePricePercentageGrowths.value * startClose / 100;
        double deltaClose = (finalClose - startClose) / (days - 1);

        double finalValue = startValue + valuePercentageGrowths.value * startValue / 100;
        double deltaValue = (finalValue - startValue) / (days - 1);

        List<StockDailyResult> dailyResults = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            double open = startOpen + deltaOpen * i;
            double close = startClose + deltaClose * i;
            double value = startValue + deltaValue * i;
            double volume = (long) value / ((open + close) / 2);
            dailyResults.add(
                StockDailyResult.builder()
                    .secId(ticker)
                    .tradeDate(startDate.plusDays(i))
                    .open(open)
                    .close(close)
                    .value(value)
                    .volume(volume)
                    .build()
            );
        }

        return dailyResults;
    }

    public List<StockTrade> generateStockTrades(StockTradesGeneratorConfig config) {
        List<StockTrade> stockTrades = new ArrayList<>();
        for (int i = 0; i < config.getValuePercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startValue = getStartValue(config, stockTrades);
            Double startPrice = getStartPrice(config, stockTrades);
            int tradeNumber = getTradeNumber(stockTrades);
            long numTrades = (long) (config.getNumTrades() * config.getValuePercentageGrowths().get(i).getWeight());
            LocalDate nowDate = config.getDate();
            LocalTime startTime = getStartTime(config, stockTrades);
            stockTrades.addAll(
                generateStockTradesBatch(
                    config.getPricePercentageGrowths().get(i),
                    config.getValuePercentageGrowths().get(i),
                    tradeNumber,
                    startPrice,
                    numTrades,
                    startValue,
                    ticker,
                    startTime,
                    nowDate
                ));
        }

        return stockTrades;
    }

    private LocalTime getStartTime(StockTradesGeneratorConfig config, List<StockTrade> stockTrades) {
        return stockTrades.isEmpty() ? config.getStartTime() : (LocalTime) stockTrades
            .get(stockTrades.size() - 1)
            .getTradeTime()
            .getValue();
    }

    private int getTradeNumber(List<StockTrade> stockTrades) {
        return stockTrades.isEmpty() ? 1 : (Integer) stockTrades.get(stockTrades.size() - 1).getTradeNo().getValue();
    }

    private Double getStartPrice(StockTradesGeneratorConfig config, List<StockTrade> stockTrades) {
        return stockTrades.isEmpty() ? config.getStartPrice() : (Double) stockTrades
            .get(stockTrades.size() - 1)
            .getPrice()
            .getValue();
    }

    private Double getStartValue(StockTradesGeneratorConfig config, List<StockTrade> stockTrades) {
        return stockTrades.isEmpty() ? config.getStartValue() : (Double) stockTrades
            .get(stockTrades.size() - 1)
            .getValue()
            .getValue();
    }

    private List<StockTrade> generateStockTradesBatch(
        PercentageGrowths pricePercentageGrowths,
        PercentageGrowths valuePercentageGrowths,
        int tradeNumber,
        Double startPrice,
        long numTrades,
        Double startValue,
        String ticker,
        LocalTime startTime,
        LocalDate nowDate
    ) {
        int buyQnt = (int) numTrades / 2;
        int sellQnt = (int) numTrades / 2;
        if (valuePercentageGrowths.value > 5) {
            buyQnt = buyQnt + sellQnt / 2;
            sellQnt = sellQnt - sellQnt / 2;
        }
        if (valuePercentageGrowths.value < 0) {
            buyQnt = buyQnt - buyQnt / 2;
            sellQnt = sellQnt + buyQnt / 2;
        }
        double finalPrice = startPrice + pricePercentageGrowths.value * startPrice / 100;
        double deltaPrice = (finalPrice - startPrice) / (numTrades - 1);

        double finalValue = startValue + valuePercentageGrowths.value * startValue / 100;
        double deltaValue = (finalValue - startValue) / (numTrades - 1);

        List<StockTrade> stockTrades = new ArrayList<>();

        for (int i = 0; i < numTrades; i++) {
            String buysell = buysell(buyQnt > 0, sellQnt > 0);
            if (buysell.equals("SELL")) {
                sellQnt--;
            }
            if (buysell.equals("BUY")) {
                buyQnt--;
            }
            stockTrades.add(
                StockTrade.builder()
                    .secId(ticker)
                    .tradeNo(tradeNumber + i)
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

    public String buysell(boolean buyIsPossible, boolean sellIsPossible) {
        if (!buyIsPossible) return "SELL";
        if (!sellIsPossible) return "BUY";
        if (Math.random() > 0.5) {
            return "BUY";
        } else {
            return "SELL";
        }
    }
}
