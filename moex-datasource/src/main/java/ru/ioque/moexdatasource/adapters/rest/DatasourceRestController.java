package ru.ioque.moexdatasource.adapters.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.moexdatasource.adapters.rest.response.history.HistoryValueResponse;
import ru.ioque.moexdatasource.adapters.rest.response.instrument.InstrumentResponse;
import ru.ioque.moexdatasource.adapters.rest.response.intraday.IntradayValueResponse;
import ru.ioque.moexdatasource.application.InstrumentService;
import ru.ioque.moexdatasource.application.TradingDataService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceRestController {
    InstrumentService instrumentService;
    TradingDataService tradingDataService;

    @GetMapping("/api/instruments")
    public List<InstrumentResponse> instruments() {
        log.info("received request to get all instruments");
        return instrumentService
            .getInstruments()
            .stream()
            .map(InstrumentResponse::from)
            .toList();
    }

    @GetMapping("/api/instruments/{ticker}/history")
    public List<HistoryValueResponse> history(
        @PathVariable String ticker,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to
    ) {
        log.info("received request to get history for ticker {} from {} to {}", ticker, from, to);
        return tradingDataService
            .getHistory(ticker, from, to)
            .stream()
            .map(HistoryValueResponse::from)
            .toList();
    }

    @GetMapping("/api/instruments/{ticker}/intraday")
    public List<IntradayValueResponse> intradayValues(
        @PathVariable String ticker,
        @RequestParam(required = false, defaultValue = "0") long lastNumber
    ) {
        log.info("received request to get intraday values for ticker {}, lastNumber {}", ticker, lastNumber);
        return tradingDataService
            .getIntradayValues(ticker, lastNumber)
            .stream()
            .map(IntradayValueResponse::from)
            .toList();
    }
}
