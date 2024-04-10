package ru.ioque.core.datagenerator.generator;

import ru.ioque.core.datagenerator.intraday.Deal;
import ru.ioque.core.datagenerator.config.DealsGeneratorConfig;
import ru.ioque.core.datagenerator.core.IntradayGenerator;
import ru.ioque.core.datagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DealsGenerator extends IntradayGenerator<Deal, DealsGeneratorConfig> {
    public List<Deal> generateIntradayValues(DealsGeneratorConfig config) {
        List<Deal> stockTrades = new ArrayList<>();
        long currentNumbers = 0L;
        for (int i = 0; i < config.getValue().getPercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startValue = getStartValue(config, stockTrades);
            Double startPrice = getStartPrice(config, stockTrades);
            long number = currentNumbers + 1L;
            long numTrades = (long) (config.getNumTrades() * config.getValue().getPercentageGrowths().get(i).getWeight());
            currentNumbers = currentNumbers + numTrades;
            LocalDate nowDate = config.getDate();
            LocalTime startTime = getStartTime(config, stockTrades);
            stockTrades.addAll(
                generateStockTradesBatch(
                    config.getPrice().getPercentageGrowths().get(i),
                    config.getValue().getPercentageGrowths().get(i),
                    number,
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

    private Double getStartValue(DealsGeneratorConfig config, List<Deal> stockTrades) {
        return stockTrades.isEmpty() ? config.getValue().getStartValue() : stockTrades
            .get(stockTrades.size() - 1)
            .getValue();
    }

    private List<Deal> generateStockTradesBatch(
        PercentageGrowths pricePercentageGrowths,
        PercentageGrowths valuePercentageGrowths,
        long number,
        Double startPrice,
        long numTrades,
        Double startValue,
        String ticker,
        LocalTime startTime,
        LocalDate nowDate
    ) {
        int buyQnt = (int) numTrades / 2;
        int sellQnt = (int) numTrades / 2;
        if (valuePercentageGrowths.getValue() > 0) {
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
            double value = startValue + deltaValue * i;
            double price = startPrice + deltaPrice * i;
            stockTrades.add(
                Deal.builder()
                    .ticker(ticker)
                    .number(number + i)
                    .dateTime(nowDate.atTime(startTime.plusSeconds(i)))
                    .value(value)
                    .price(price)
                    .qnt(1)
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
