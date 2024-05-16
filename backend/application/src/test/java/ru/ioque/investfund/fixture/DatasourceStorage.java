package ru.ioque.investfund.fixture;

import lombok.Getter;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DatasourceStorage {
    private final List<IntradayData> intradayDataDtos = new ArrayList<>();
    private final List<AggregatedHistory> aggregatedHistoryDtos = new ArrayList<>();
    private final List<InstrumentDetail> instrumentDtos = new ArrayList<>();

    public List<AggregatedHistory> getHistoryDataBy(Ticker ticker) {
        return aggregatedHistoryDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
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

    public void initHistoryValues(List<AggregatedHistory> historyValues) {
        this.aggregatedHistoryDtos.clear();
        this.aggregatedHistoryDtos.addAll(historyValues);
    }
}
