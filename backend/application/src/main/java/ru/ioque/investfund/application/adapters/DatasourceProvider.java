package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.application.datasource.dto.HistoryBatch;
import ru.ioque.investfund.application.datasource.dto.InstrumentBatch;
import ru.ioque.investfund.application.datasource.dto.IntradayBatch;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

public interface DatasourceProvider {
    InstrumentBatch fetchInstrumentDetails(Datasource datasource);
    HistoryBatch fetchAggregateHistory(Datasource datasource, Instrument instrument);
    IntradayBatch fetchIntradayValues(Datasource datasource, Instrument instrument);
}