package ru.ioque.investfund.fixture;

import lombok.Getter;
import ru.ioque.investfund.application.datasource.dto.history.HistoryValueDto;
import ru.ioque.investfund.application.datasource.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.datasource.dto.intraday.IntradayValueDto;

import java.util.ArrayList;
import java.util.List;

public class DatasourceStorage {
    @Getter
    private final List<IntradayValueDto> intradayValueDtos = new ArrayList<>();
    @Getter
    private final List<HistoryValueDto> historyValueDtos = new ArrayList<>();
    @Getter
    private final List<InstrumentDto> instrumentDtos = new ArrayList<>();

    public List<HistoryValueDto> getHistoryDataBy(String ticker) {
        return historyValueDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<IntradayValueDto> getDealsByTicker(String ticker) {
        return intradayValueDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<InstrumentDto> getInstrumentBy(String ticker) {
        return instrumentDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public void initInstrumentDetails(List<InstrumentDto> details) {
        this.instrumentDtos.clear();
        this.instrumentDtos.addAll(details);
    }

    public void initDealDatas(List<IntradayValueDto> intradayValues) {
        this.intradayValueDtos.clear();
        this.intradayValueDtos.addAll(intradayValues);
    }

    public void initHistoryValues(List<HistoryValueDto> historyValues) {
        this.historyValueDtos.clear();
        this.historyValueDtos.addAll(historyValues);
    }
}
