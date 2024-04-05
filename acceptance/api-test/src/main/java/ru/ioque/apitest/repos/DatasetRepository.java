package ru.ioque.apitest.repos;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.core.dataset.DatasetStorage;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;

import java.time.LocalDate;
import java.util.List;

@Component
public class DatasetRepository {
    DatasetStorage datasetStorage = new DatasetStorage();

    @SneakyThrows
    public List<Instrument> getInstruments() {
        return datasetStorage.getInstruments();
    }

    public List<HistoryValue> getHistoryValues(
        String ticker,
        LocalDate from,
        LocalDate till
    ) {
        return datasetStorage
            .getHistoryValuesBy(ticker)
            .stream()
            .filter(row -> row.isBetween(from, till))
            .toList();
    }

    public List<IntradayValue> getIntradayValuesBy(String ticker, Integer start) {
        return datasetStorage
            .getIntradayValuesBy(ticker)
            .stream()
            .skip(start)
            .toList();
    }

    public void initInstruments(List<? extends Instrument> instruments) {
        datasetStorage.setInstruments(instruments);
    }

    public void initIntradayValues(List<? extends IntradayValue> intradayValues) {
        datasetStorage.setIntradayValues(intradayValues);
    }
    public void initHistoryValues(List<HistoryValue> dailyResultValues) {
        datasetStorage.setDailyResultValues(dailyResultValues);
    }
}
