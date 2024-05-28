package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.fixture.DatasourceStorage;

import java.time.LocalDate;
import java.util.List;

public class FakeDatasourceProvider implements DatasourceProvider {
    @Setter
    DatasourceStorage datasourceStorage = new DatasourceStorage();
    DateTimeProvider dateTimeProvider;

    public FakeDatasourceProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<InstrumentDetail> fetchInstruments(Datasource datasource) {
        return datasourceStorage.getInstrumentDtos();
    }

    @Override
    public List<AggregatedHistory> fetchAggregateHistory(
        Datasource datasource,
        Instrument instrument,
        LocalDate from,
        LocalDate to) {
        return datasourceStorage.getHistoryDataBy(instrument.getTicker());
    }

    @Override
    public List<IntradayData> fetchIntradayValues(
        Datasource datasource,
        Instrument instrument,
        Long from) {
        return datasourceStorage.getDealsByTicker(instrument.getTicker());
    }
}
