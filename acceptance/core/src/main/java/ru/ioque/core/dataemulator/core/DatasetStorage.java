package ru.ioque.core.dataemulator.core;

import java.util.ArrayList;
import java.util.List;

public class DatasetStorage {
    List<? extends InstrumentValue> instruments = new ArrayList<>();
    List<? extends IntradayValue> intradayValues;
    List<? extends DailyResultValue> dailyResultValues;

    public List<? extends InstrumentValue> getInstrumentsBy(InstrumentType instrumentType) {
        return instruments.stream().filter(instrument -> instrument.equalsBy(instrumentType)).toList();
    }

    public List<? extends IntradayValue> getIntradayValuesBy(String ticker) {
        return intradayValues
            .stream()
            .filter(row -> row.equalsBy(ticker))
            .toList();
    }

    public List<? extends DailyResultValue> getHistoryValuesBy(String ticker) {
        return dailyResultValues
            .stream()
            .filter(row -> row.equalsBy(ticker))
            .toList();
    }

    public void setInstruments(List<? extends InstrumentValue> instruments) {
        this.instruments = instruments;
    }

    public void setIntradayValues(List<? extends IntradayValue> intradayValues) {
        this.intradayValues = intradayValues;
    }

    public void setDailyResultValues(List<? extends DailyResultValue> dailyResultValues) {
        this.dailyResultValues = dailyResultValues;
    }
}
