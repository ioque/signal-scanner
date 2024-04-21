package ru.ioque.investfund.fixture;

import lombok.Getter;
import ru.ioque.investfund.application.datasource.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayDataDto;

import java.util.ArrayList;
import java.util.List;

public class DatasourceStorage {
    @Getter
    private final List<IntradayDataDto> intradayDataDtos = new ArrayList<>();
    @Getter
    private final List<AggregatedHistoryDto> aggregatedHistoryDtos = new ArrayList<>();
    @Getter
    private final List<InstrumentDto> instrumentDtos = new ArrayList<>();

    public List<AggregatedHistoryDto> getHistoryDataBy(String ticker) {
        return aggregatedHistoryDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<IntradayDataDto> getDealsByTicker(String ticker) {
        return intradayDataDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<InstrumentDto> getInstrumentBy(String ticker) {
        return instrumentDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public void initInstrumentDetails(List<InstrumentDto> details) {
        this.instrumentDtos.clear();
        this.instrumentDtos.addAll(details);
    }

    public void initDealDatas(List<IntradayDataDto> intradayValues) {
        this.intradayDataDtos.clear();
        this.intradayDataDtos.addAll(intradayValues);
    }

    public void initHistoryValues(List<AggregatedHistoryDto> historyValues) {
        this.aggregatedHistoryDtos.clear();
        this.aggregatedHistoryDtos.addAll(historyValues);
    }
}
