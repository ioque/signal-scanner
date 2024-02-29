package ru.ioque.acceptance.application.tradingdatagenerator.stock;

import ru.ioque.acceptance.application.tradingdatagenerator.core.IntradayGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StockIntradayGenerator extends IntradayGenerator<StockTrade, StockTradesGeneratorConfig> {
    public List<StockTrade> generateIntradayValues(StockTradesGeneratorConfig config) {
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
        if (valuePercentageGrowths.getValue() > 5) {
            buyQnt = buyQnt + sellQnt / 2;
            sellQnt = sellQnt - sellQnt / 2;
        }
        if (valuePercentageGrowths.getValue() < 0) {
            buyQnt = buyQnt - buyQnt / 2;
            sellQnt = sellQnt + buyQnt / 2;
        }
        double finalPrice = linearGrowthFinalResult(pricePercentageGrowths.getValue(), startPrice);
        double deltaPrice = getDeltaByMean(startPrice, finalPrice, numTrades);

        double finalValue = linearGrowthFinalResult(valuePercentageGrowths.getValue(), startValue);
        double deltaValue = getDeltaByMean(startValue, finalValue, numTrades);

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
