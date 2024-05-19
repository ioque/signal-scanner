package ru.ioque.apitest.dataset;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dataset.DatasetStorage;
import ru.ioque.core.dataset.DatasetStorageFactory;

import java.time.LocalDate;
import java.util.List;

@Component
public class DatasetRepository {
    public DatasetStorage datasetStorage;

    public void init(Dataset dataset) {
        datasetStorage = DatasetStorageFactory.createDatasetStorage(dataset);
    }

    @SneakyThrows
    public List<Instrument> getInstruments() {
        return datasetStorage.getInstruments();
    }

    public List<HistoryValue> getHistoryValues(
        String ticker,
        LocalDate from,
        LocalDate till
    ) {
        return datasetStorage.getHistoryValuesBy(ticker, from, till);
    }

    public List<IntradayValue> getIntradayValuesBy(String ticker, Integer lastNumber) {
        return datasetStorage.getIntradayValuesBy(ticker, lastNumber);
    }
}
