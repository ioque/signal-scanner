package ru.ioque.moexdatasource.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.moexdatasource.application.adapters.InstrumentRepo;
import ru.ioque.moexdatasource.application.adapters.MoexProvider;
import ru.ioque.moexdatasource.domain.history.HistoryValue;
import ru.ioque.moexdatasource.domain.intraday.IntradayValue;

import java.util.List;

@Component
@AllArgsConstructor
public class TradingDataService {
    InstrumentRepo instrumentRepo;
    MoexProvider moexProvider;
    public List<HistoryValue> getHistory(String ticker) {
        return instrumentRepo
            .findBy(ticker)
            .map(instrument -> instrument.parseHistoryValues(moexProvider.fetchHistory(instrument)))
            .orElseThrow();
    }

    public List<IntradayValue> getIntradayValues(String ticker) {
        return instrumentRepo
            .findBy(ticker)
            .map(instrument -> instrument.parseIntradayValues(moexProvider.fetchIntradayValues(instrument)))
            .orElseThrow();
    }
}
