package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.datasource.integration.dto.history.AggregateHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayValueDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;

public interface DatasourceProvider {
    List<InstrumentDto> fetchInstruments(Datasource datasource);
    List<AggregateHistoryDto> fetchAggregateHistory(Datasource datasource, Instrument instrument);
    List<IntradayValueDto> fetchIntradayValues(Datasource datasource, Instrument instrument);
}