package ru.ioque.acceptance.application.tradingdatagenerator.currencypair;

import ru.ioque.acceptance.application.tradingdatagenerator.core.IntradayGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairTrade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CurrencyPairIntradayGenerator extends IntradayGenerator<CurrencyPairTrade, CurrencyPairTradeGeneratorConfig> {
    public List<CurrencyPairTrade> generateIntradayValues(CurrencyPairTradeGeneratorConfig config) {
        List<CurrencyPairTrade> currencyPairTrades = new ArrayList<>();
        for (int i = 0; i < config.getValue().getPercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startValue = getStartValue(config, currencyPairTrades);
            Double startPrice = getStartPrice(config, currencyPairTrades);
            int tradeNumber = getTradeNumber(currencyPairTrades);
            long numTrades = (long) (config.getNumTrades() * config.getValue().getPercentageGrowths().get(i).getWeight());
            LocalDate nowDate = config.getDate();
            LocalTime startTime = getStartTime(config, currencyPairTrades);
            currencyPairTrades.addAll(
                generateBatch(
                    config.getPrice().getPercentageGrowths().get(i),
                    config.getValue().getPercentageGrowths().get(i),
                    tradeNumber,
                    startPrice,
                    numTrades,
                    startValue,
                    ticker,
                    startTime,
                    nowDate
                ));
        }

        return currencyPairTrades;
    }

    private Double getStartValue(CurrencyPairTradeGeneratorConfig config, List<CurrencyPairTrade> stockTrades) {
        return stockTrades.isEmpty() ? config.getValue().getStartValue() : (Double) stockTrades
            .get(stockTrades.size() - 1)
            .getValue()
            .getValue();
    }

    private List<CurrencyPairTrade> generateBatch(
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

        List<CurrencyPairTrade> stockTrades = new ArrayList<>();

        for (int i = 0; i < numTrades; i++) {
            String buysell = buysell(buyQnt > 0, sellQnt > 0);
            if (buysell.equals("SELL")) {
                sellQnt--;
            }
            if (buysell.equals("BUY")) {
                buyQnt--;
            }
            stockTrades.add(
                CurrencyPairTrade.builder()
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
