package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.HistoryBatch;
import ru.ioque.investfund.domain.datasource.value.InstrumentBatch;
import ru.ioque.investfund.domain.datasource.value.IntradayBatch;
import ru.ioque.investfund.fixture.DatasourceStorage;

public class FakeDatasourceProvider implements DatasourceProvider {
    @Setter
    DatasourceStorage datasourceStorage = new DatasourceStorage();
    DateTimeProvider dateTimeProvider;

    public FakeDatasourceProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public HistoryBatch fetchHistoryBy(
        Datasource datasource, Instrument instrument
    ) {
        return new HistoryBatch(
            instrument.getTicker(),
            datasourceStorage.getHistoryDataBy(instrument.getId()).stream().toList()
        );
    }

    @Override
    public IntradayBatch fetchIntradayValuesBy(Datasource datasource, Instrument instrument) {
        return new IntradayBatch(
            instrument.getTicker(),
            datasourceStorage.getDealsByTicker(instrument.getId())
        );
    }

    @Override
    public InstrumentBatch fetchInstruments(Datasource datasource) {
        return new InstrumentBatch(
            datasource.getId(),
            datasourceStorage.getInstruments()
        );
    }
}
