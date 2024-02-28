package ru.ioque.acceptance.application.tradingdatagenerator;

import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockDailyResultGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockIntradayValueGenerator;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;

import java.util.List;

public class TradingDataGeneratorFacade {
    StockDailyResultGenerator stockDailyResultGenerator = new StockDailyResultGenerator();
    StockIntradayValueGenerator stockIntradayValueGenerator = new StockIntradayValueGenerator();
    public List<StockDailyResult> generateStockHistory(StockHistoryGeneratorConfig config) {
        return stockDailyResultGenerator.generateStockHistory(config);
    }

    public List<StockTrade> generateStockTrades(StockTradesGeneratorConfig config) {
        return stockIntradayValueGenerator.generateStockTrades(config);
    }
}
