package ru.ioque.acceptance.application.tradingdatagenerator.futures;

import ru.ioque.acceptance.application.tradingdatagenerator.core.IntradayGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesTrade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FuturesIntradayGenerator extends IntradayGenerator<FuturesTrade, FuturesTradesGeneratorConfig> {
    public List<FuturesTrade> generateIntradayValues(FuturesTradesGeneratorConfig config) {
        List<FuturesTrade> futuresTrades = new ArrayList<>();
        for (int i = 0; i < config.getPrice().getPercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startPrice = getStartPrice(config, futuresTrades);
            int tradeNumber = getTradeNumber(futuresTrades);
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

    private List<FuturesTrade> generateBatch(
        PercentageGrowths pricePercentageGrowths,
        int tradeNumber,
        Double startPrice,
        long numTrades,
        String ticker,
        LocalTime startTime,
        LocalDate nowDate
    ) {
        double finalPrice = linearGrowthFinalResult(pricePercentageGrowths.getValue(), startPrice);
        double deltaPrice = getDeltaByMean(startPrice, finalPrice, numTrades);

        List<FuturesTrade> stockTrades = new ArrayList<>();

        for (int i = 0; i < numTrades; i++) {
            stockTrades.add(
                FuturesTrade.builder()
                    .secId(ticker)
                    .tradeNo(tradeNumber + i)
                    .tradeTime(startTime.plusSeconds(i))
                    .price(startPrice + deltaPrice * i)
                    .sysTime(nowDate.atTime(startTime.plusSeconds(i)))
                    .build()
            );
        }

        return stockTrades;
    }
}
