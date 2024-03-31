package ru.ioque.apitest.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.apitest.repos.DatasetRepository;
import ru.ioque.core.model.history.HistoryValue;
import ru.ioque.core.model.instrument.Instrument;
import ru.ioque.core.model.intraday.IntradayValue;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FakeMoexRestController {
    DatasetRepository datasetRepository;

    @SneakyThrows
    @GetMapping(value = "/api/instruments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Instrument> getInstruments(
    ) {
        return datasetRepository.getInstruments();
    }

    @GetMapping("/api/instruments/{ticker}/intraday")
    public List<IntradayValue> getIntraday(
        @PathVariable String ticker,
        @RequestParam(required = false, defaultValue = "0") int start
    ) {
        return datasetRepository.getIntradayValuesBy(ticker, start);
    }

    @GetMapping("/api/instruments/{ticker}/history")
    public List<HistoryValue> getHistory(
        @PathVariable String ticker,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to
    ) {
        return datasetRepository.getHistoryValues(ticker, from, to);
    }
}
