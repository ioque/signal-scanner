package ru.ioque.moexdatasource.adapters.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DatasourceRestController {
    InstrumentService instrumentService;
    TradingDataService tradingDataService;

    @GetMapping("/api/instruments")
    public List<InstrumentResponse> instruments() {
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
        return tradingDataService
            .getHistory(ticker, from, to)
            .stream()
            .map(HistoryValueResponse::from)
            .toList();
    }

    @GetMapping("/api/instruments/{ticker}/intraday")
    public List<IntradayValueResponse> intradayValues(
        @PathVariable String ticker,
        @RequestParam(required = false, defaultValue = "0") int start
    ) {
        return tradingDataService
            .getIntradayValues(ticker, start)
            .stream()
            .map(IntradayValueResponse::from)
            .toList();
    }
}
