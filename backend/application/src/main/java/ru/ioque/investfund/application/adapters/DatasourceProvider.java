package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.util.List;
import java.util.TreeSet;

public interface DatasourceProvider {
    List<InstrumentDetails> fetchInstrumentDetails(Datasource datasource);
    TreeSet<HistoryValue> fetchHistoryBy(Datasource datasource, Instrument instrument);
    TreeSet<IntradayValue> fetchIntradayValuesBy(Datasource datasource, Instrument instrument);
}