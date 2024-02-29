package ru.ioque.acceptance.application.tradingdatagenerator;

import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.currencypair.CurrencyPairDailyResultGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.currencypair.CurrencyPairTradeGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.futures.FuturesDailyResultGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.futures.FuturesTradesGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.index.IndexDailyResultGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.index.IndexDeltasGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockDailyResultGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockIntradayGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockTradesGeneratorConfig;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairDailyResult;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairTrade;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesDailyResult;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesTrade;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDailyResult;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDelta;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;

import java.util.List;

public class TradingDataGeneratorFacade {
    StockDailyResultGenerator stockDailyResultGenerator = new StockDailyResultGenerator();
    IndexDailyResultGenerator indexDailyResultGenerator = new IndexDailyResultGenerator();
    FuturesDailyResultGenerator futuresDailyResultGenerator = new FuturesDailyResultGenerator();
    CurrencyPairDailyResultGenerator currencyPairDailyResultGenerator = new CurrencyPairDailyResultGenerator();
    StockIntradayGenerator stockIntradayValueGenerator = new StockIntradayGenerator();
    public List<StockDailyResult> generateStockHistory(HistoryGeneratorConfig config) {
        return stockDailyResultGenerator.generateHistory(config);
    }

    public List<StockTrade> generateStockTrades(StockTradesGeneratorConfig config) {
        return stockIntradayValueGenerator.generateStockTrades(config);
    }

    public List<IndexDailyResult> generateIndexHistory(HistoryGeneratorConfig config) {
        return indexDailyResultGenerator.generateHistory(config);
    }

    public List<IndexDelta> generateIndexDeltas(IndexDeltasGeneratorConfig config) {
        return List.of();
    }

    public List<CurrencyPairDailyResult> generateCurrencyPairHistory(HistoryGeneratorConfig config) {
        return currencyPairDailyResultGenerator.generateHistory(config);
    }

    public List<CurrencyPairTrade> generateCurrencyPairTrades(CurrencyPairTradeGeneratorConfig config) {
        return List.of();
    }

    public List<FuturesDailyResult> generateFuturesHistory(HistoryGeneratorConfig config) {
        return futuresDailyResultGenerator.generateHistory(config);
    }

    public List<FuturesTrade> generateFuturesTrades(FuturesTradesGeneratorConfig config) {
        return List.of();
    }
}
