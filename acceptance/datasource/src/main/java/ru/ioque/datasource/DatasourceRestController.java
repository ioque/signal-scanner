package ru.ioque.datasource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;
import ru.ioque.core.dataset.Dataset;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceRestController {
    DatasetRepository datasetRepository;

    @SneakyThrows
    @PostMapping(value = "/api/dataset", produces = MediaType.APPLICATION_JSON_VALUE)
    public void initDataset(@RequestBody Dataset dataset) {
        datasetRepository.initInstruments(dataset.getInstruments());
        datasetRepository.initIntradayValue(dataset.getIntradayValues());
        datasetRepository.initHistoryValues(dataset.getHistoryValues());
    }

    @SneakyThrows
    @GetMapping(value = "/api/instruments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Instrument> getInstruments(
    ) {
        return datasetRepository.getInstruments();
    }

    @GetMapping(value = "/api/instruments/{ticker}/intraday", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IntradayValue> getIntraday(
        @PathVariable String ticker,
        @RequestParam(required = false, defaultValue = "0") int start
    ) {
        return datasetRepository.getIntradayValuesBy(ticker, start);
    }

    @GetMapping(value = "/api/instruments/{ticker}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoryValue> getHistory(
        @PathVariable String ticker,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to
    ) {
        return datasetRepository.getHistoryValues(ticker, from, to);
    }
}
