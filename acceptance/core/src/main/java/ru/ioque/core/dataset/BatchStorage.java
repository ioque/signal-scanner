package ru.ioque.core.dataset;

import lombok.Getter;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BatchStorage implements DatasetStorage {
    List<Instrument> instruments = new ArrayList<>();
    List<IntradayValue> intradayValues = new ArrayList<>();
    List<HistoryValue> historyValues = new ArrayList<>();

    public BatchStorage(Dataset dataset) {
        setInstruments(dataset.getInstruments());
        setIntradayValues(dataset.getIntradayValues());
        setHistoryValues(dataset.getHistoryValues());
    }

    public List<IntradayValue> getIntradayValuesBy(String ticker, Integer start) {
        return intradayValues
                .stream()
                .filter(row -> row.getTicker().equals(ticker))
                .skip(start)
                .toList();
    }

    public List<HistoryValue> getHistoryValuesBy(String ticker, LocalDate from, LocalDate till) {
        return historyValues
                .stream()
                .filter(row -> row.getTicker().equals(ticker) && row.isBetween(from, till))
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

    public void setHistoryValues(List<HistoryValue> historyValues) {
        this.historyValues.clear();
        this.historyValues.addAll(historyValues);
    }
}
