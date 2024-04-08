package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.List;

public interface DatasourceProvider {
    List<Instrument> fetchInstruments(Datasource datasource);
    List<HistoryValue> fetchHistoryBy(Datasource datasource, Instrument instrument);
    List<IntradayValue> fetchIntradayValuesBy(Datasource datasource, Instrument instrument);
}