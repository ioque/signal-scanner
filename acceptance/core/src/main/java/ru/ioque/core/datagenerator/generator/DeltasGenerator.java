package ru.ioque.core.datagenerator.generator;

import ru.ioque.core.datagenerator.intraday.Delta;
import ru.ioque.core.datagenerator.config.DeltasGeneratorConfig;
import ru.ioque.core.datagenerator.core.IntradayGenerator;
import ru.ioque.core.datagenerator.core.PercentageGrowths;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DeltasGenerator extends IntradayGenerator<Delta, DeltasGeneratorConfig> {
    public List<Delta> generateIntradayValues(DeltasGeneratorConfig config) {
        List<Delta> indexDeltas = new ArrayList<>();
        for (int i = 0; i < config.getValue().getPercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startValue = getStartValue(config, indexDeltas);
            Double startPrice = getStartPrice(config, indexDeltas);
            long tradeNumber = getTradeNumber(indexDeltas);
            long numTrades = (long) (config.getNumTrades() * config.getValue().getPercentageGrowths().get(i).getWeight());
            LocalDate nowDate = config.getDate();
            LocalTime startTime = getStartTime(config, indexDeltas);
            indexDeltas.addAll(
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

        return indexDeltas;
    }

    protected Double getStartValue(DeltasGeneratorConfig config, List<Delta> stockTrades) {
        return stockTrades.isEmpty() ? config.getValue().getStartValue() : stockTrades
            .get(stockTrades.size() - 1)
            .getValue();
    }

    private List<Delta> generateBatch(
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
        double finalPrice = linearGrowthFinalResult(pricePercentageGrowths.getValue(), startPrice);
        double deltaPrice = getDeltaByMean(startPrice, finalPrice, numTrades);

        double finalValue = linearGrowthFinalResult(valuePercentageGrowths.getValue(), startValue);
        double deltaValue = getDeltaByMean(startValue, finalValue, numTrades);

        List<Delta> indexDeltas = new ArrayList<>();

        for (int i = 0; i < numTrades; i++) {
            indexDeltas.add(
                Delta.builder()
                    .ticker(ticker)
                    .tradeNumber(tradeNumber + i)
                    .dateTime(nowDate.atTime(startTime.plusSeconds(i * 10L)))
                    .value(startValue + deltaValue * i)
                    .price(startPrice + deltaPrice * i)
                    .build()
            );
        }

        return indexDeltas;
    }
}
