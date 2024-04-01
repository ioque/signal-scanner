package ru.ioque.core.dataset;

import lombok.Getter;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DatasetStorage {
    List<Instrument> instruments = new ArrayList<>();
    List<IntradayValue> intradayValues = new ArrayList<>();
    List<HistoryValue> dailyResultValues = new ArrayList<>();

    public List<IntradayValue> getIntradayValuesBy(String ticker) {
        return intradayValues
            .stream()
            .filter(row -> row.getTicker().equals(ticker))
            .toList();
    }

    public List<HistoryValue> getHistoryValuesBy(String ticker) {
        return dailyResultValues
            .stream()
            .filter(row -> row.getTicker().equals(ticker))
            .toList();
    }

    public void setInstruments(List<? extends Instrument> instruments) {
        this.instruments.clear();
        this.instruments.addAll(instruments);
    }

    public void setIntradayValues(List<? extends IntradayValue> intradayValues) {
        this.intradayValues.clear();
        this.intradayValues.addAll(intradayValues);
    }

    public void setDailyResultValues(List<HistoryValue> dailyResultValues) {
        this.dailyResultValues.clear();
        this.dailyResultValues.addAll(dailyResultValues);
    }
}