package ru.ioque.acceptance.application.tradingdatagenerator.core;

import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockTradesGeneratorConfig;
import ru.ioque.acceptance.domain.dataemulator.core.IntradayValue;

import java.time.LocalTime;
import java.util.List;

public abstract class IntradayGenerator<T extends IntradayValue, C extends GeneratorConfig> extends AbstractGenerator<T> {
    public abstract List<T> generateIntradayValues(C generatorConfig);

    protected LocalTime getStartTime(StockTradesGeneratorConfig config, List<T> stockTrades) {
        return stockTrades.isEmpty() ? config.getStartTime() : (LocalTime) stockTrades
            .get(stockTrades.size() - 1)
            .getTradeTime()
            .getValue();
    }

    protected int getTradeNumber(List<T> stockTrades) {
        return stockTrades.isEmpty() ? 1 : (Integer) stockTrades.get(stockTrades.size() - 1).getTradeNo().getValue();
    }

    protected Double getStartPrice(StockTradesGeneratorConfig config, List<T> stockTrades) {
        return stockTrades.isEmpty() ? config.getStartPrice() : (Double) stockTrades
            .get(stockTrades.size() - 1)
            .getPrice()
            .getValue();
    }
}
