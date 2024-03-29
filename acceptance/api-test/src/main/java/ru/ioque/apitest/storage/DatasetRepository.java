package ru.ioque.apitest.storage;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.core.dataemulator.core.DailyResultValue;
import ru.ioque.core.dataemulator.core.DatasetStorage;
import ru.ioque.core.dataemulator.core.InstrumentType;
import ru.ioque.core.dataemulator.core.InstrumentValue;
import ru.ioque.core.dataemulator.core.IntradayValue;

import java.time.LocalDate;
import java.util.List;

@Component
public class DatasetRepository {
    DatasetStorage datasetStorage = new DatasetStorage();

    @SneakyThrows
    public List<? extends InstrumentValue> getInstrumentsBy(InstrumentType instrumentType) {
        return datasetStorage.getInstrumentsBy(instrumentType);
    }

    public List<? extends DailyResultValue> getHistoryValuesBy(
        String ticker,
        LocalDate from,
        LocalDate till,
        Integer limit,
        Integer start
    ) {
        return datasetStorage
            .getHistoryValuesBy(ticker)
            .stream()
            .filter(row -> row.isBetween(from, till))
            .skip(start)
            .limit(limit)
            .toList();
    }

    public List<? extends IntradayValue> getIntradayValuesBy(String ticker, Integer limit, Integer start) {
        return datasetStorage
            .getIntradayValuesBy(ticker)
            .stream()
            .skip(start)
            .limit(limit)
            .toList();
    }

    public void initInstruments(List<? extends InstrumentValue> instruments) {
        datasetStorage.setInstruments(instruments);
    }

    public void initIntradayValue(List<? extends IntradayValue> intradayValues) {
        datasetStorage.setIntradayValues(intradayValues);
    }
    public void initDailyResultValue(List<? extends DailyResultValue> dailyResultValues) {
        datasetStorage.setDailyResultValues(dailyResultValues);
    }
}
