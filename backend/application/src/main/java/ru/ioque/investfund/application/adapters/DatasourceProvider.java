package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.time.LocalDate;
import java.util.List;

public interface DatasourceProvider {
    List<InstrumentDetail> fetchInstruments(Datasource datasource);
    List<AggregatedTotals> fetchAggregateHistory(Datasource datasource, Instrument instrument, LocalDate from, LocalDate to);
    List<IntradayData> fetchIntradayValues(Datasource datasource, Instrument instrument, Long from);
}