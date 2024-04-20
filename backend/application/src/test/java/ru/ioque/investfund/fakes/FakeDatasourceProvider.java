package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
import ru.ioque.investfund.fixture.DatasourceStorage;

import java.util.List;
import java.util.TreeSet;

public class FakeDatasourceProvider implements DatasourceProvider {
    @Setter
    DatasourceStorage datasourceStorage = new DatasourceStorage();
    DateTimeProvider dateTimeProvider;

    public FakeDatasourceProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public TreeSet<HistoryValue> fetchHistoryBy(Datasource datasource, Instrument instrument) {
        return new TreeSet<>(datasourceStorage.getHistoryDataBy(instrument.getTicker()).stream().toList());
    }

    @Override
    public TreeSet<IntradayValue> fetchIntradayValuesBy(Datasource datasource, Instrument instrument) {
        return new TreeSet<>(datasourceStorage.getDealsByTicker(instrument.getTicker()));
    }

    @Override
    public List<InstrumentDetails> fetchInstrumentDetails(Datasource datasource) {
        return datasourceStorage.getInstrumentDetails();
    }
}
