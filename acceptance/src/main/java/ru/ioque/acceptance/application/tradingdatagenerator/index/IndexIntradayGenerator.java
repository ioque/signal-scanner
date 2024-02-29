package ru.ioque.acceptance.application.tradingdatagenerator.index;

import ru.ioque.acceptance.application.tradingdatagenerator.core.IntradayGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDelta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class IndexIntradayGenerator extends IntradayGenerator<IndexDelta, IndexDeltasGeneratorConfig> {
    public List<IndexDelta> generateIntradayValues(IndexDeltasGeneratorConfig config) {
        List<IndexDelta> indexDeltas = new ArrayList<>();
        for (int i = 0; i < config.getValue().getPercentageGrowths().size(); i++) {
            String ticker = config.getTicker();
            Double startValue = getStartValue(config, indexDeltas);
            Double startPrice = getStartPrice(config, indexDeltas);
            int tradeNumber = getTradeNumber(indexDeltas);
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

    protected Double getStartValue(IndexDeltasGeneratorConfig config, List<IndexDelta> stockTrades) {
        return stockTrades.isEmpty() ? config.getValue().getStartValue() : (Double) stockTrades
            .get(stockTrades.size() - 1)
            .getValue()
            .getValue();
    }

    private List<IndexDelta> generateBatch(
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
        double finalPrice = linearGrowthFinalResult(pricePercentageGrowths.getValue(), startPrice);
        double deltaPrice = getDeltaByMean(startPrice, finalPrice, numTrades);

        double finalValue = linearGrowthFinalResult(valuePercentageGrowths.getValue(), startValue);
        double deltaValue = getDeltaByMean(startValue, finalValue, numTrades);

        List<IndexDelta> indexDeltas = new ArrayList<>();

        for (int i = 0; i < numTrades; i++) {
            indexDeltas.add(
                IndexDelta.builder()
                    .secId(ticker)
                    .tradeNo(tradeNumber + i)
                    .tradeTime(startTime.plusSeconds(i))
                    .value(startValue + deltaValue * i)
                    .price(startPrice + deltaPrice * i)
                    .sysTime(nowDate.atTime(startTime.plusSeconds(i)))
                    .build()
            );
        }

        return indexDeltas;
    }
}
