package ru.ioque.investfund.domain.scanner.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public class SearchContext {

    Map<Ticker, Instrument> instruments = new HashMap<>();
    Map<InstrumentId, Ticker> idToTickerMap = new HashMap<>();

    public SearchContext(List<Instrument> instruments) {
        instruments.forEach(instrument -> {
            this.instruments.put(instrument.getTicker(), instrument);
            this.idToTickerMap.put(instrument.getId(), instrument.getTicker());
        });
    }

    public Optional<Instrument> getInstrumentBy(InstrumentId instrumentId) {
        return getInstrumentBy(idToTickerMap.get(instrumentId));
    }

    public Optional<Instrument> getInstrumentBy(Ticker ticker) {
        return Optional
            .ofNullable(instruments.get(ticker));
    }
}
