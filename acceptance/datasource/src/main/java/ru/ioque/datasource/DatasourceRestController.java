package ru.ioque.datasource;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dataset.DatasetStorage;
import ru.ioque.core.dataset.DatasetStorageFactory;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatasourceRestController {
    DatasetStorage datasetStorage;

    @SneakyThrows
    @PostMapping(value = "/api/dataset", produces = MediaType.APPLICATION_JSON_VALUE)
    public void initDataset(@RequestBody Dataset dataset) {
        log.info("received new dataset: {}", dataset.toString());
        datasetStorage = DatasetStorageFactory.createDatasetStorage(dataset);
    }

    @SneakyThrows
    @GetMapping(value = "/api/instruments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Instrument> getInstruments() throws IllegalAccessException {
        log.info("received getInstruments request");
        if (datasetStorage == null) {
            throw new IllegalAccessException("Не иницилиазирован датасет");
        }
        return datasetStorage.getInstruments();
    }

    @GetMapping(value = "/api/instruments/{ticker}/intraday", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IntradayValue> getIntraday(
        @PathVariable String ticker,
        @RequestParam(required = false, defaultValue = "0") int start
    ) throws IllegalAccessException {
        log.info("received getIntraday request {ticker={}, start={}", ticker, start);
        if (datasetStorage == null) {
            throw new IllegalAccessException("Не иницилиазирован датасет");
        }
        return datasetStorage.getIntradayValuesBy(ticker, start);
    }

    @GetMapping(value = "/api/instruments/{ticker}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoryValue> getHistory(
        @PathVariable String ticker,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to
    ) throws IllegalAccessException {
        log.info("received getHistory request {ticker={}, from={}, to={}", ticker, from, to);
        if (datasetStorage == null) {
            throw new IllegalAccessException("Не иницилиазирован датасет");
        }
        return datasetStorage.getHistoryValuesBy(ticker, from, to);
    }
}
