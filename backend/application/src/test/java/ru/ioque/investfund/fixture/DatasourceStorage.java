package ru.ioque.investfund.fixture;

import lombok.Getter;
import ru.ioque.investfund.application.datasource.integration.dto.history.AggregateHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayValueDto;

import java.util.ArrayList;
import java.util.List;

public class DatasourceStorage {
    @Getter
    private final List<IntradayValueDto> intradayValueDtos = new ArrayList<>();
    @Getter
    private final List<AggregateHistoryDto> aggregateHistoryDtos = new ArrayList<>();
    @Getter
    private final List<InstrumentDto> instrumentDtos = new ArrayList<>();

    public List<AggregateHistoryDto> getHistoryDataBy(String ticker) {
        return aggregateHistoryDtos.stream().filter(row -> row.getTicker().equals(ticker)).toList();
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

    public void initHistoryValues(List<AggregateHistoryDto> historyValues) {
        this.aggregateHistoryDtos.clear();
        this.aggregateHistoryDtos.addAll(historyValues);
    }
}
