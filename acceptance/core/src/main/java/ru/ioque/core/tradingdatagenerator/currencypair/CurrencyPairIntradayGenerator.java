package ru.ioque.core.tradingdatagenerator.currencypair;

import ru.ioque.core.model.intraday.Deal;
import ru.ioque.core.tradingdatagenerator.core.IntradayGenerator;
import ru.ioque.core.tradingdatagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CurrencyPairIntradayGenerator extends
    IntradayGenerator<Deal, CurrencyPairTradeGeneratorConfig> {
    public List<Deal> generateIntradayValues(CurrencyPairTradeGeneratorConfig config) {
        List<Deal> currencyPairTrades = new ArrayList<>();
        for (int i = 0; i < config.getValue().getPercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startValue = getStartValue(config, currencyPairTrades);
            Double startPrice = getStartPrice(config, currencyPairTrades);
            long tradeNumber = getTradeNumber(currencyPairTrades);
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

    private Double getStartValue(CurrencyPairTradeGeneratorConfig config, List<Deal> stockTrades) {
        return stockTrades.isEmpty() ? config.getValue().getStartValue() : (Double) stockTrades
            .get(stockTrades.size() - 1)
            .getValue();
    }

    private List<Deal> generateBatch(
        PercentageGrowths pricePercentageGrowths,
        PercentageGrowths valuePercentageGrowths,
        long tradeNumber,
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

        List<Deal> stockTrades = new ArrayList<>();

        for (int i = 0; i < numTrades; i++) {
            Boolean isBuy = isBuy(buyQnt > 0, sellQnt > 0);
            if (isBuy) {
                buyQnt--;
            } else {
                sellQnt--;
            }
            stockTrades.add(
                Deal.builder()
                    .ticker(ticker)
                    .tradeNumber(tradeNumber + i)
                    .dateTime(nowDate.atTime(startTime.plusSeconds(i)))
                    .value(startValue + deltaValue * i)
                    .price(startPrice + deltaPrice * i)
                    .isBuy(isBuy)
                    .build()
            );
        }

        return stockTrades;
    }

    public Boolean isBuy(boolean buyIsPossible, boolean sellIsPossible) {
        if (!buyIsPossible) return true;
        if (!sellIsPossible) return false;
        return Math.random() > 0.5;
    }
}
