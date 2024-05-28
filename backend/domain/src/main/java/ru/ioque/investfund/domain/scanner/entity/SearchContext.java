package ru.ioque.investfund.domain.scanner.entity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.IntradayStatistic;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public class SearchContext {

    Map<Ticker, Instrument> instruments;
    Map<InstrumentId, Ticker> idToTickerMap;

    public SearchContext(List<Instrument> instruments) {
        instruments.forEach(instrument -> {
            this.instruments.put(instrument.getTicker(), instrument);
            this.idToTickerMap.put(instrument.getId(), instrument.getTicker());
        });
    }

    public Instrument getInstrumentBy(InstrumentId instrumentId) {
        return instruments.get(idToTickerMap.get(instrumentId));
    }

    public Instrument getInstrumentBy(Ticker ticker) {
        return instruments.get(ticker);
    }

    public void updateStockPerformance(IntradayStatistic intradayStatistic) {
        Optional
            .ofNullable(instruments.get(intradayStatistic.getTicker()))
            .ifPresent(instrument -> instrument.updatePerformance(intradayStatistic));
    }
}
