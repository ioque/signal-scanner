package ru.ioque.investfund.domain.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

public class PipelineContext {
    DatasourceId datasourceId;
    Map<Ticker, InstrumentId> tickerToIdMap = new HashMap<>();
    Map<InstrumentId, IntradayPerformance> intradayPerformances = new HashMap<>();
    Map<InstrumentId, TreeSet<AggregatedTotals>> aggregatedHistories = new HashMap<>();

    public PipelineContext(DatasourceId datasourceId, Set<Instrument> instruments) {
        this.datasourceId = datasourceId;
        for (Instrument instrument : instruments) {
            tickerToIdMap.put(instrument.getTicker(), instrument.getId());
            this.intradayPerformances.put(instrument.getId(), new IntradayPerformance());
            this.aggregatedHistories.put(instrument.getId(), new TreeSet<>());
        }
    }

    public void updateIntradayPerformance(IntradayPerformance intradayPerformance) {
        if (!tickerToIdMap.containsKey(intradayPerformance.getTicker())) {
            throw new DomainException("");
        }
        intradayPerformances.put(tickerToIdMap.get(intradayPerformance.getTicker()), intradayPerformance);
    }

    public void addAggregatedHistory(AggregatedTotals aggregatedTotals) {
        if (!tickerToIdMap.containsKey(aggregatedTotals.getTicker())) {
            throw new DomainException("");
        }
        aggregatedHistories.get(tickerToIdMap.get(aggregatedTotals.getTicker())).add(aggregatedTotals);
    }
}
