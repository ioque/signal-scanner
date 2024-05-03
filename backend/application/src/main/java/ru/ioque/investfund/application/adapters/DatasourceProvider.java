package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;

public interface DatasourceProvider {
    List<InstrumentDto> fetchInstruments(Datasource datasource);
    List<AggregatedHistoryDto> fetchAggregateHistory(Datasource datasource, Instrument instrument);
    List<IntradayDataDto> fetchIntradayValues(Datasource datasource, Instrument instrument);
}