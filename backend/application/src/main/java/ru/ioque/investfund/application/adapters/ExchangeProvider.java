package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeProvider {
    List<Instrument> fetchInstruments(String exchangeUrl);
    List<HistoryValue> fetchHistoryBy(String exchangeUrl, String ticker, LocalDate from, LocalDate to);
    List<IntradayValue> fetchIntradayValuesBy(String exchangeUrl, String ticker, long lastNumber);
}