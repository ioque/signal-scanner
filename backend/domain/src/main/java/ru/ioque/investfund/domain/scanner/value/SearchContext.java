package ru.ioque.investfund.domain.scanner.value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public class SearchContext {

    Map<Ticker, InstrumentPerformance> instruments = new HashMap<>();
    Map<InstrumentId, Ticker> idToTickerMap = new HashMap<>();

    public SearchContext(List<InstrumentPerformance> instrumentPerformances) {
        instrumentPerformances.forEach(performance -> {
            this.instruments.put(performance.getTicker(), performance);
            this.idToTickerMap.put(performance.getInstrumentId(), performance.getTicker());
        });
    }

    public Optional<InstrumentPerformance> getInstrumentBy(InstrumentId instrumentId) {
        return getInstrumentBy(idToTickerMap.get(instrumentId));
    }

    public Optional<InstrumentPerformance> getInstrumentBy(Ticker ticker) {
        return Optional
            .ofNullable(instruments.get(ticker));
    }
}
