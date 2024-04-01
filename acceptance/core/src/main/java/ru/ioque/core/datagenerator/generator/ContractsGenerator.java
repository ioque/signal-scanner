package ru.ioque.core.datagenerator.generator;

import ru.ioque.core.datagenerator.intraday.Contract;
import ru.ioque.core.datagenerator.config.ContractsGeneratorConfig;
import ru.ioque.core.datagenerator.core.IntradayGenerator;
import ru.ioque.core.datagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ContractsGenerator extends IntradayGenerator<Contract, ContractsGeneratorConfig> {
    public List<Contract> generateIntradayValues(ContractsGeneratorConfig config) {
        List<Contract> futuresTrades = new ArrayList<>();
        for (int i = 0; i < config.getPrice().getPercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startPrice = getStartPrice(config, futuresTrades);
            long number = getTradeNumber(futuresTrades);
            long numTrades = (long) (config.getNumTrades() * config.getPrice().getPercentageGrowths().get(i).getWeight());
            LocalDate nowDate = config.getDate();
            LocalTime startTime = getStartTime(config, futuresTrades);
            futuresTrades.addAll(
                generateBatch(
                    config.getPrice().getPercentageGrowths().get(i),
                    number,
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
        long number,
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
                    .number(number + i)
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
