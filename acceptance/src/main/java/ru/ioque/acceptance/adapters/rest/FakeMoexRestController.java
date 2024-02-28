package ru.ioque.acceptance.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.acceptance.application.datasource.DatasetManager;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentType;

import java.time.LocalDate;
import java.util.Map;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FakeMoexRestController {
    ObjectMapper objectMapper;
    DatasetManager datasetManager;

    @SneakyThrows
    @GetMapping(value = "/iss/engines/{engine}/markets/{market}/boards/{board}/securities.json", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getInstruments(@PathVariable String engine, @PathVariable String market, @PathVariable String board) {
        return getInstrumentsBy(board);
    }

    @GetMapping("/iss/engines/{engine}/markets/{market}/boards/{board}/securities/{ticker}/trades.json")
    ResponseEntity<String> getStockDeals(@PathVariable String ticker,
                                         @PathVariable String board,
                                         @PathVariable String engine,
                                         @PathVariable String market,
                                         @RequestParam Integer limit,
                                         @RequestParam Integer start
    ) {
        return getIntradayValuesBy(ticker, limit, start);
    }

    @GetMapping("/iss/history/engines/{engine}/markets/{market}/boards/{board}/securities/{ticker}.json")
    ResponseEntity<String> getStockHistory(
        @PathVariable String ticker,
        @PathVariable String board,
        @PathVariable String engine,
        @PathVariable String market,
        @RequestParam LocalDate from,
        @RequestParam LocalDate till,
        @RequestParam Integer limit,
        @RequestParam Integer start
    ) {
        return getHistoryValues(ticker, from, till, limit, start);
    }

    private ResponseEntity<String> getHistoryValues(String ticker, LocalDate from, LocalDate till, Integer limit, Integer start) {
        return new ResponseEntity<>(
            toJson(HistoryResponse.fromBy(datasetManager.getHistoryValuesBy(ticker, from, till, limit, start))),
            HttpStatus.OK
        );
    }

    private ResponseEntity<String> getIntradayValuesBy(String ticker, Integer limit, Integer start) {
        return new ResponseEntity<>(
            toJson(TradesResponse.fromBy(datasetManager.getIntradayValuesBy(ticker, limit, start))),
            HttpStatus.OK
        );
    }

    private ResponseEntity<String> getInstrumentsBy(String board) {
        return new ResponseEntity<>(
            toJson(SecuritiesResponse.fromBy(datasetManager.getInstrumentsBy(typeMap.get(board)))),
            HttpStatus.OK
        );
    }

    private final Map<String, InstrumentType> typeMap = Map.of(
        "TQBR", InstrumentType.STOCK,
        "RFUD", InstrumentType.FUTURES,
        "CETS", InstrumentType.CURRENCY,
        "SNDX", InstrumentType.INDEX
    );

    @SneakyThrows
    private String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }
}
