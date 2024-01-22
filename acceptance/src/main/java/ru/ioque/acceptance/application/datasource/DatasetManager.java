package ru.ioque.acceptance.application.datasource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.application.adapters.DateTimeProvider;
import ru.ioque.acceptance.domain.dataemulator.core.DatasetStorage;
import ru.ioque.acceptance.domain.dataemulator.core.HistoryValue;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentType;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntradayValue;

import javax.naming.OperationNotSupportedException;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasetManager {
    DatasetStorage dataSetStorage = new DatasetStorage();
    DateTimeProvider dateTimeProvider;

    @SneakyThrows
    public List<? extends InstrumentValue> getInstrumentsBy(InstrumentType instrumentType) {
        return dataSetStorage.getInstrumentsBy(instrumentType);
    }

    public List<? extends HistoryValue> getHistoryValuesBy(
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

    public void initDataset(List<? extends InstrumentValue> instruments) {
        dataSetStorage.initInstruments(instruments);
    }

    @SneakyThrows
    public void initFromDatabase() {
        throw new OperationNotSupportedException();
    }
}
