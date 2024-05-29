package ru.ioque.investfund.fakes;

import lombok.Getter;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DatasourceStorage {
    private final List<IntradayData> intradayDataDtos = new ArrayList<>();
    private final List<AggregatedTotals> aggregatedTotalsDtos = new ArrayList<>();
    private final List<InstrumentDetail> instrumentDtos = new ArrayList<>();

    public List<AggregatedTotals> getHistoryDataBy(Ticker ticker) {
        return aggregatedTotalsDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<IntradayData> getDealsByTicker(Ticker ticker) {
        return intradayDataDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public void initInstrumentDetails(List<InstrumentDetail> details) {
        this.instrumentDtos.clear();
        this.instrumentDtos.addAll(details);
    }

    public void initDealDatas(List<IntradayData> intradayValues) {
        this.intradayDataDtos.clear();
        this.intradayDataDtos.addAll(intradayValues);
    }

    public void initHistoryValues(List<AggregatedTotals> historyValues) {
        this.aggregatedTotalsDtos.clear();
        this.aggregatedTotalsDtos.addAll(historyValues);
    }
}
