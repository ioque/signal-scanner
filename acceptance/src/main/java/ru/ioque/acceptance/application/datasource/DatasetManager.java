package ru.ioque.acceptance.application.datasource;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.application.datasource.datasets.DefaultDataset;
import ru.ioque.acceptance.domain.dataemulator.core.DailyResultValue;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetStorage;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentType;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntradayValue;

import java.time.LocalDate;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasetManager {
    DatasetStorage dataSetStorage = new DatasetStorage();

    public DatasetManager() {
        initInstruments(DefaultDataset.getInstruments());
        initIntradayValue(DefaultDataset.getIntradayValues());
        initDailyResultValue(DefaultDataset.getDailyResults());
    }

    @SneakyThrows
    public List<? extends InstrumentValue> getInstrumentsBy(InstrumentType instrumentType) {
        return dataSetStorage.getInstrumentsBy(instrumentType);
    }

    public List<? extends DailyResultValue> getHistoryValuesBy(
        String ticker,
        LocalDate from,
        LocalDate till,
        Integer limit,
        Integer start
    ) {
        return dataSetStorage
            .getHistoryValuesBy(ticker)
            .stream()
            .filter(row -> row.isBetween(from, till))
            .skip(start)
            .limit(limit)
            .toList();
    }

    public List<? extends IntradayValue> getIntradayValuesBy(String ticker, Integer limit, Integer start) {
        return dataSetStorage
            .getIntradayValuesBy(ticker)
            .stream()
            .skip(start)
            .limit(limit)
            .toList();
    }

    public void initInstruments(List<? extends InstrumentValue> instruments) {
        dataSetStorage.setInstruments(instruments);
    }

    public void initIntradayValue(List<? extends IntradayValue> intradayValues) {
        dataSetStorage.setIntradayValues(intradayValues);
    }
    public void initDailyResultValue(List<? extends DailyResultValue> dailyResultValues) {
        dataSetStorage.setDailyResultValues(dailyResultValues);
    }
}
