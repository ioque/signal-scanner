package ru.ioque.investfund.fixture;

import lombok.Getter;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.ArrayList;
import java.util.List;

public class DatasourceStorage {
    @Getter
    private final List<IntradayValue> intradayValues = new ArrayList<>();
    @Getter
    private final List<HistoryValue> historyValues = new ArrayList<>();
    @Getter
    private final List<InstrumentDetails> instrumentDetails = new ArrayList<>();

    public List<HistoryValue> getHistoryDataBy(Ticker ticker) {
        return historyValues.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<IntradayValue> getDealsByTicker(Ticker ticker) {
        return intradayValues.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<InstrumentDetails> getInstrumentDetailsBy(Ticker ticker) {
        return instrumentDetails.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public void initInstrumentDetails(List<InstrumentDetails> details) {
        this.instrumentDetails.clear();
        this.instrumentDetails.addAll(details);
    }

    public void initDealDatas(List<IntradayValue> intradayValues) {
        this.intradayValues.clear();
        this.intradayValues.addAll(intradayValues);
    }

    public void initTradingResults(List<HistoryValue> historyValues) {
        this.historyValues.clear();
        this.historyValues.addAll(historyValues);
    }
}
