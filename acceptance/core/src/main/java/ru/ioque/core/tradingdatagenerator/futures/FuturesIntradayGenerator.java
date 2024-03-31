package ru.ioque.core.tradingdatagenerator.futures;

import ru.ioque.core.model.intraday.Contract;
import ru.ioque.core.tradingdatagenerator.core.IntradayGenerator;
import ru.ioque.core.tradingdatagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FuturesIntradayGenerator extends IntradayGenerator<Contract, FuturesTradesGeneratorConfig> {
    public List<Contract> generateIntradayValues(FuturesTradesGeneratorConfig config) {
        List<Contract> futuresTrades = new ArrayList<>();
        for (int i = 0; i < config.getPrice().getPercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startPrice = getStartPrice(config, futuresTrades);
            long tradeNumber = getTradeNumber(futuresTrades);
            long numTrades = (long) (config.getNumTrades() * config.getPrice().getPercentageGrowths().get(i).getWeight());
            LocalDate nowDate = config.getDate();
            LocalTime startTime = getStartTime(config, futuresTrades);
            futuresTrades.addAll(
                generateBatch(
                    config.getPrice().getPercentageGrowths().get(i),
                    tradeNumber,
                    startPrice,
                    numTrades,
                    ticker,
                    startTime,
                    nowDate
                ));
        }

        return futuresTrades;
    }

    private List<Contract> generateBatch(
        PercentageGrowths pricePercentageGrowths,
        long tradeNumber,
        Double startPrice,
        long numTrades,
        String ticker,
        LocalTime startTime,
        LocalDate nowDate
    ) {
        double finalPrice = linearGrowthFinalResult(pricePercentageGrowths.getValue(), startPrice);
        double deltaPrice = getDeltaByMean(startPrice, finalPrice, numTrades);

        List<Contract> stockTrades = new ArrayList<>();

        for (int i = 0; i < numTrades; i++) {
            stockTrades.add(
                Contract.builder()
                    .ticker(ticker)
                    .tradeNumber(tradeNumber + i)
                    .dateTime(nowDate.atTime(startTime.plusSeconds(i)))
                    .price(startPrice + deltaPrice * i)
                    .value(10000D)
                    .qnt(10)
                    .build()
            );
        }

        return stockTrades;
    }
}
