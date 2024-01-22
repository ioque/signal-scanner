package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;
import ru.ioque.investfund.fixture.ExchangeDataFixture;

import java.util.List;

public class FakeExchangeProvider implements ExchangeProvider {
    @Setter
    ExchangeDataFixture exchangeDataFixture = new ExchangeDataFixture();
    DateTimeProvider dateTimeProvider;

    public FakeExchangeProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<DailyValue> fetchDailyTradingResultsBy(
        Instrument instrument
    ) {
        return exchangeDataFixture.getHistoryDataByTicker(instrument.getTicker()).stream().toList();
    }

    @Override
    public List<IntradayValue> fetchIntradayValuesBy(Instrument instrument) {
        return exchangeDataFixture.getDealsByTicker(instrument.getTicker());
    }

    @Override
    public List<Instrument> fetchInstruments() {
        return exchangeDataFixture.getInstruments();
    }
}
