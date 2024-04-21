package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.datasource.integration.dto.history.AggregateHistoryDto;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.datasource.integration.dto.intraday.IntradayValueDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.fixture.DatasourceStorage;

import java.util.List;

public class FakeDatasourceProvider implements DatasourceProvider {
    @Setter
    DatasourceStorage datasourceStorage = new DatasourceStorage();
    DateTimeProvider dateTimeProvider;

    public FakeDatasourceProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<AggregateHistoryDto> fetchAggregateHistory(Datasource datasource, Instrument instrument) {
        return datasourceStorage.getHistoryDataBy(instrument.getTicker().getValue());
    }

    @Override
    public List<IntradayValueDto> fetchIntradayValues(Datasource datasource, Instrument instrument) {
        return datasourceStorage.getDealsByTicker(instrument.getTicker().getValue());
    }

    @Override
    public List<InstrumentDto> fetchInstruments(Datasource datasource) {
        return datasourceStorage.getInstrumentDtos();
    }
}
