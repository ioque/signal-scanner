package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayBatch;

import java.util.List;

public interface DatasourceProvider {
    List<Instrument> fetchInstruments(Datasource datasource);
    HistoryBatch fetchHistoryBy(Datasource datasource, Instrument instrument);
    IntradayBatch fetchIntradayValuesBy(Datasource datasource, Instrument instrument);
}