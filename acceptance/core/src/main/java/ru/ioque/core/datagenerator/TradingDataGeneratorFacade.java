package ru.ioque.core.datagenerator;

import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.intraday.Contract;
import ru.ioque.core.datagenerator.intraday.Deal;
import ru.ioque.core.datagenerator.intraday.Delta;
import ru.ioque.core.datagenerator.core.HistoryGenerator;
import ru.ioque.core.datagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.datagenerator.generator.ContractsGenerator;
import ru.ioque.core.datagenerator.config.ContractsGeneratorConfig;
import ru.ioque.core.datagenerator.config.DeltasGeneratorConfig;
import ru.ioque.core.datagenerator.generator.DeltasGenerator;
import ru.ioque.core.datagenerator.generator.DealsGenerator;
import ru.ioque.core.datagenerator.config.DealsGeneratorConfig;

import java.util.List;

public class TradingDataGeneratorFacade {
    HistoryGenerator historyGenerator = new HistoryGenerator();
    DealsGenerator stockIntradayValueGenerator = new DealsGenerator();
    ContractsGenerator contractsGenerator = new ContractsGenerator();
    DeltasGenerator deltasGenerator = new DeltasGenerator();
    public List<HistoryValue> generateHistory(HistoryGeneratorConfig config) {
        return historyGenerator.generateHistory(config);
    }

    public List<Deal> generateDeals(DealsGeneratorConfig config) {
        return stockIntradayValueGenerator.generateIntradayValues(config);
    }

    public List<Delta> generateDeltas(DeltasGeneratorConfig config) {
        return deltasGenerator.generateIntradayValues(config);
    }

    public List<Contract> generateContracts(ContractsGeneratorConfig config) {
        return contractsGenerator.generateIntradayValues(config);
    }
}
