package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.fixture.ExchangeDataFixture;

import java.util.List;

public class FakeDatasourceProvider implements DatasourceProvider {
    @Setter
    ExchangeDataFixture exchangeDataFixture = new ExchangeDataFixture();
    DateTimeProvider dateTimeProvider;

    public FakeDatasourceProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<HistoryValue> fetchHistoryBy(
        Datasource datasource, Instrument instrument
    ) {
        return exchangeDataFixture.getHistoryDataByTicker(instrument.getTicker()).stream().toList();
    }

    @Override
    public List<IntradayValue> fetchIntradayValuesBy(Datasource datasource, Instrument instrument) {
        return exchangeDataFixture.getDealsByTicker(instrument.getTicker());
    }

    @Override
    public List<Instrument> fetchInstruments(Datasource datasource) {
        return exchangeDataFixture.getInstruments();
    }
}
