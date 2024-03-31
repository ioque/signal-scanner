package ru.ioque.core.tradingdatagenerator;

import ru.ioque.core.model.history.HistoryValue;
import ru.ioque.core.model.intraday.Contract;
import ru.ioque.core.model.intraday.Deal;
import ru.ioque.core.model.intraday.Delta;
import ru.ioque.core.tradingdatagenerator.core.HistoryGenerator;
import ru.ioque.core.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.futures.FuturesIntradayGenerator;
import ru.ioque.core.tradingdatagenerator.futures.FuturesTradesGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.index.IndexDeltasGeneratorConfig;
import ru.ioque.core.tradingdatagenerator.index.IndexIntradayGenerator;
import ru.ioque.core.tradingdatagenerator.stock.DealGenerator;
import ru.ioque.core.tradingdatagenerator.stock.DealGeneratorConfig;

import java.util.List;

public class TradingDataGeneratorFacade {
    HistoryGenerator historyGenerator = new HistoryGenerator();
    DealGenerator stockIntradayValueGenerator = new DealGenerator();
    FuturesIntradayGenerator futuresIntradayGenerator = new FuturesIntradayGenerator();
    IndexIntradayGenerator indexIntradayGenerator = new IndexIntradayGenerator();
    public List<HistoryValue> generateHistory(HistoryGeneratorConfig config) {
        return historyGenerator.generateHistory(config);
    }

    public List<Deal> generateDeals(DealGeneratorConfig config) {
        return stockIntradayValueGenerator.generateIntradayValues(config);
    }

    public List<Delta> generateDeltas(IndexDeltasGeneratorConfig config) {
        return indexIntradayGenerator.generateIntradayValues(config);
    }

    public List<Contract> generateContracts(FuturesTradesGeneratorConfig config) {
        return futuresIntradayGenerator.generateIntradayValues(config);
    }
}
