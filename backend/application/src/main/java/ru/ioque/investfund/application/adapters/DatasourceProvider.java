package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.history.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayBatch;

import java.util.List;

public interface DatasourceProvider {
    List<InstrumentDetails> fetchInstrumentDetails(Datasource datasource);
    HistoryBatch fetchHistoryBy(Datasource datasource, Instrument instrument);
    IntradayBatch fetchIntradayValuesBy(Datasource datasource, Instrument instrument);
}