package ru.ioque.core.tradingdatagenerator.core;

import ru.ioque.core.model.intraday.IntradayValue;

import java.time.LocalTime;
import java.util.List;

public abstract class IntradayGenerator<T extends IntradayValue, C extends IntradayGeneratorConfig> extends AbstractGenerator {
    public abstract List<T> generateIntradayValues(C generatorConfig);

    protected LocalTime getStartTime(C config, List<T> stockTrades) {
        return stockTrades.isEmpty() ?
            config.getStartTime() :
            stockTrades.get(stockTrades.size() - 1).getDateTime().toLocalTime();
    }

    protected long getTradeNumber(List<T> stockTrades) {
        return stockTrades.isEmpty() ?
            1 :
            stockTrades.get(stockTrades.size() - 1).getTradeNumber();
    }

    protected Double getStartPrice(C config, List<T> stockTrades) {
        return stockTrades.isEmpty() ?
            config.getPrice().getStartValue() :
            stockTrades.get(stockTrades.size() - 1).getPrice();
    }
}
