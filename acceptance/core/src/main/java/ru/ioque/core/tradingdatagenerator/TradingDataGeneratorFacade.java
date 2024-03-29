package ru.ioque.core.tradingdatagenerator;

import ru.ioque.core.dataemulator.currencyPair.CurrencyPairDailyResult;
import ru.ioque.core.dataemulator.currencyPair.CurrencyPairTrade;
import ru.ioque.core.dataemulator.futures.FuturesDailyResult;
import ru.ioque.core.dataemulator.futures.FuturesTrade;
import ru.ioque.core.dataemulator.index.IndexDailyResult;
import ru.ioque.core.dataemulator.index.IndexDelta;
import ru.ioque.core.dataemulator.stock.StockDailyResult;
import ru.ioque.core.dataemulator.stock.StockTrade;
import ru.ioque.core.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.currencypair.CurrencyPairDailyResultGenerator;
import ru.ioque.core.tradingdatagenerator.currencypair.CurrencyPairIntradayGenerator;
import ru.ioque.core.tradingdatagenerator.currencypair.CurrencyPairTradeGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.futures.FuturesDailyResultGenerator;
import ru.ioque.core.tradingdatagenerator.futures.FuturesIntradayGenerator;
import ru.ioque.core.tradingdatagenerator.futures.FuturesTradesGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.index.IndexDailyResultGenerator;
import ru.ioque.core.tradingdatagenerator.index.IndexDeltasGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.index.IndexIntradayGenerator;
import ru.ioque.core.tradingdatagenerator.stock.StockDailyResultGenerator;
import ru.ioque.core.tradingdatagenerator.stock.StockIntradayGenerator;
import ru.ioque.core.tradingdatagenerator.stock.StockTradesGeneratorConfig;

import java.util.List;

public class TradingDataGeneratorFacade {
    StockDailyResultGenerator stockDailyResultGenerator = new StockDailyResultGenerator();
    IndexDailyResultGenerator indexDailyResultGenerator = new IndexDailyResultGenerator();
    FuturesDailyResultGenerator futuresDailyResultGenerator = new FuturesDailyResultGenerator();
    CurrencyPairDailyResultGenerator currencyPairDailyResultGenerator = new CurrencyPairDailyResultGenerator();
    StockIntradayGenerator stockIntradayValueGenerator = new StockIntradayGenerator();
    CurrencyPairIntradayGenerator currencyPairIntradayGenerator = new CurrencyPairIntradayGenerator();
    FuturesIntradayGenerator futuresIntradayGenerator = new FuturesIntradayGenerator();
    IndexIntradayGenerator indexIntradayGenerator = new IndexIntradayGenerator();
    public List<StockDailyResult> generateStockHistory(HistoryGeneratorConfig config) {
        return stockDailyResultGenerator.generateHistory(config);
    }

    public List<StockTrade> generateStockTrades(StockTradesGeneratorConfig config) {
        return stockIntradayValueGenerator.generateIntradayValues(config);
    }

    public List<IndexDailyResult> generateIndexHistory(HistoryGeneratorConfig config) {
        return indexDailyResultGenerator.generateHistory(config);
    }

    public List<IndexDelta> generateIndexDeltas(IndexDeltasGeneratorConfig config) {
        return indexIntradayGenerator.generateIntradayValues(config);
    }

    public List<CurrencyPairDailyResult> generateCurrencyPairHistory(HistoryGeneratorConfig config) {
        return currencyPairDailyResultGenerator.generateHistory(config);
    }

    public List<CurrencyPairTrade> generateCurrencyPairTrades(CurrencyPairTradeGeneratorConfig config) {
        return currencyPairIntradayGenerator.generateIntradayValues(config);
    }

    public List<FuturesDailyResult> generateFuturesHistory(HistoryGeneratorConfig config) {
        return futuresDailyResultGenerator.generateHistory(config);
    }

    public List<FuturesTrade> generateFuturesTrades(FuturesTradesGeneratorConfig config) {
        return futuresIntradayGenerator.generateIntradayValues(config);
    }
}
