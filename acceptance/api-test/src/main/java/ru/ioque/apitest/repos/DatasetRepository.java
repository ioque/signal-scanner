package ru.ioque.apitest.repos;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.core.model.history.HistoryValue;
import ru.ioque.core.model.instrument.Instrument;
import ru.ioque.core.model.intraday.IntradayValue;

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

    public void initIntradayValue(List<? extends IntradayValue> intradayValues) {
        datasetStorage.setIntradayValues(intradayValues);
    }
    public void initDailyResultValue(List<HistoryValue> dailyResultValues) {
        datasetStorage.setDailyResultValues(dailyResultValues);
    }
}
