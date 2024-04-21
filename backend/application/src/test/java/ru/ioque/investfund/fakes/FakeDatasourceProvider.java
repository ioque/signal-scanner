package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.datasource.dto.HistoryBatch;
import ru.ioque.investfund.application.datasource.dto.InstrumentBatch;
import ru.ioque.investfund.application.datasource.dto.IntradayBatch;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.fixture.DatasourceStorage;

public class FakeDatasourceProvider implements DatasourceProvider {
    @Setter
    DatasourceStorage datasourceStorage = new DatasourceStorage();
    DateTimeProvider dateTimeProvider;

    public FakeDatasourceProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public HistoryBatch fetchAggregateHistory(Datasource datasource, Instrument instrument) {
        return new HistoryBatch(datasourceStorage.getHistoryDataBy(instrument.getTicker().getValue()));
    }

    @Override
    public IntradayBatch fetchIntradayValues(Datasource datasource, Instrument instrument) {
        return new IntradayBatch(datasourceStorage.getDealsByTicker(instrument.getTicker().getValue()));
    }

    @Override
    public InstrumentBatch fetchInstrumentDetails(Datasource datasource) {
        return new InstrumentBatch(datasourceStorage.getInstrumentDtos());
    }
}
