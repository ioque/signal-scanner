package ru.ioque.moexdatasource.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.moexdatasource.application.adapters.InstrumentRepo;
import ru.ioque.moexdatasource.application.adapters.MoexProvider;
import ru.ioque.moexdatasource.domain.history.HistoryValue;
import ru.ioque.moexdatasource.domain.intraday.IntradayValue;
import ru.ioque.moexdatasource.domain.parser.HistoryValueMoexParser;
import ru.ioque.moexdatasource.domain.parser.IntradayValueMoexParser;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TradingDataService {
    IntradayValueMoexParser intradayValueParser = new IntradayValueMoexParser();
    HistoryValueMoexParser historyValueParser = new HistoryValueMoexParser();
    InstrumentRepo instrumentRepo;
    MoexProvider moexProvider;

    public List<HistoryValue> getHistory(String ticker, LocalDate from, LocalDate to) {
        return instrumentRepo
            .findBy(ticker)
            .map(instrument ->
                historyValueParser.parse(
                    moexProvider.fetchHistory(instrument, from, to),
                    instrument.getClass()
                )
            )
            .orElseThrow();
    }

    public List<IntradayValue> getIntradayValues(String ticker, long lastNumber) {
        return instrumentRepo
            .findBy(ticker)
            .map(instrument ->
                intradayValueParser.parse(
                    moexProvider.fetchIntradayValues(instrument, lastNumber),
                    instrument
                )
            )
            .orElseThrow();
    }
}
