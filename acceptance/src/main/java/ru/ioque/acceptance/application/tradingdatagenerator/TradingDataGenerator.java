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
                        .tradeDate(config.getStartDate().plusDays(i))
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
        return stockTrades.isEmpty() ? config.getStartTime() : (LocalTime) stockTrades.get(stockTrades.size() - 1).getTradeTime().getValue();
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
