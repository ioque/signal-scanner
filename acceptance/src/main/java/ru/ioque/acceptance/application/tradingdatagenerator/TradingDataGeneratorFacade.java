package ru.ioque.acceptance.application.tradingdatagenerator;

import ru.ioque.acceptance.application.tradingdatagenerator.currencypair.CurrencyPairHistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.currencypair.CurrencyPairTradeGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.futures.FuturesHistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.futures.FuturesTradesGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.index.IndexDeltasGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.index.IndexHistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockDailyResultGenerator;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockHistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockIntradayValueGenerator;
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
    StockIntradayValueGenerator stockIntradayValueGenerator = new StockIntradayValueGenerator();
    public List<StockDailyResult> generateStockHistory(StockHistoryGeneratorConfig config) {
        return stockDailyResultGenerator.generateStockHistory(config);
    }

    public List<StockTrade> generateStockTrades(StockTradesGeneratorConfig config) {
        return stockIntradayValueGenerator.generateStockTrades(config);
    }

    public List<IndexDailyResult> generateIndexHistory(IndexHistoryGeneratorConfig config) {
        return List.of();
    }

    public List<IndexDelta> generateIndexDeltas(IndexDeltasGeneratorConfig config) {
        return List.of();
    }

    public List<CurrencyPairDailyResult> generateCurrencyPairHistory(CurrencyPairHistoryGeneratorConfig config) {
        return List.of();
    }

    public List<CurrencyPairTrade> generateCurrencyPairTrades(CurrencyPairTradeGeneratorConfig config) {
        return List.of();
    }

    public List<FuturesDailyResult> generateFuturesHistory(FuturesHistoryGeneratorConfig config) {
        return List.of();
    }

    public List<FuturesTrade> generateFuturesTrades(FuturesTradesGeneratorConfig config) {
        return List.of();
    }
}
