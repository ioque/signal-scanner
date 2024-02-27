package ru.ioque.investfund.adapters.exchange.emulator;

import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.util.List;

public class Datasource {
    List<DailyValue> dailyValues;
    List<IntradayValue> intradayValues;
    List<Instrument> instruments;

    public void initDailyValue(List<DailyValue> dailyValues) {
        this.dailyValues.clear();
        this.dailyValues.addAll(dailyValues);
    }

    public void initIntradayValue(List<IntradayValue> intradayValues) {
        this.intradayValues.clear();
        this.intradayValues.addAll(intradayValues);
    }

    public void initInstruments(List<Instrument> instruments) {
        this.instruments.clear();
        this.instruments.addAll(instruments);
    }

    public List<DailyValue> getDailyValue(String ticker) {
        return dailyValues.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<IntradayValue> getIntradayValue(String ticker) {
        return intradayValues.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public List<Instrument> getInstruments() {
        return instruments;
    }
}
