package ru.ioque.acceptance.domain.dataemulator.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatasetStorage {
    List<? extends InstrumentValue> instruments = new ArrayList<>();

    public void initInstruments(List<? extends InstrumentValue> instruments) {
        this.instruments = instruments;
    }

    public List<? extends InstrumentValue> getInstrumentsBy(InstrumentType instrumentType) {
        return instruments.stream().filter(instrument -> instrument.equalsBy(instrumentType)).toList();
    }

    public List<? extends IntradayValue> getIntradayValuesBy(String ticker) {
        return instruments
            .stream()
            .filter(row -> row.equalsBy(ticker))
            .findFirst()
            .map(InstrumentValue::getIntradayValues)
            .stream()
            .flatMap(Collection::stream)
            .toList();
    }

    public List<? extends HistoryValue> getHistoryValuesBy(String ticker) {
        return instruments
            .stream()
            .filter(row -> row.equalsBy(ticker))
            .findFirst()
            .map(InstrumentValue::getHistoryValues)
            .stream()
            .flatMap(Collection::stream)
            .toList();
    }
}
