package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.util.List;

public interface ExchangeProvider {
    List<DailyValue> fetchDailyTradingResultsBy(Instrument instrument);
    List<IntradayValue> fetchIntradayValuesBy(Instrument instrument);
    List<Instrument> fetchInstruments();
}