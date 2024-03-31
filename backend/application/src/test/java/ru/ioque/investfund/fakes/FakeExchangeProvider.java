package ru.ioque.investfund.fakes;

import lombok.Setter;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;
import ru.ioque.investfund.fixture.ExchangeDataFixture;

import java.time.LocalDate;
import java.util.List;

public class FakeExchangeProvider implements ExchangeProvider {
    @Setter
    ExchangeDataFixture exchangeDataFixture = new ExchangeDataFixture();
    DateTimeProvider dateTimeProvider;

    public FakeExchangeProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public List<HistoryValue> fetchHistoryBy(
        String ticker, LocalDate from, LocalDate to
    ) {
        return exchangeDataFixture.getHistoryDataByTicker(ticker).stream().toList();
    }

    @Override
    public List<IntradayValue> fetchIntradayValuesBy(String ticker, int start) {
        return exchangeDataFixture.getDealsByTicker(ticker);
    }

    @Override
    public List<Instrument> fetchInstruments() {
        return exchangeDataFixture.getInstruments();
    }
}
