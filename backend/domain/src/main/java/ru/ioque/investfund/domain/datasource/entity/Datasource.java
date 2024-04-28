package ru.ioque.investfund.domain.datasource.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.Domain;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Datasource extends Domain<DatasourceId> {
    String name;
    String url;
    String description;
    final List<Instrument> instruments;

    @Builder
    public Datasource(
        DatasourceId id,
        String name,
        String url,
        String description,
        List<Instrument> instruments
    ) {
        super(id);
        this.name = name;
        this.url = url;
        this.description = description;
        this.instruments = instruments;
    }

    public void updateName(String name) {
        this.name = name;
    }
    public void updateUrl(String url) {
        this.url = url;
    }
    public void updateDescription(String description) {
        this.description = description;
    }

    public List<InstrumentId> findInstrumentIds(List<Ticker> tickers) {
        Map<Ticker, InstrumentId> tickerToIdsMap = getTickerToIdsMap();
        List<Ticker> notExistedInstrument = tickers
            .stream()
            .filter(ticker -> !tickerToIdsMap.containsKey(ticker))
            .toList();
        if (!notExistedInstrument.isEmpty()) {
            throw new IllegalArgumentException(
                String
                    .format(
                        "В выбранном источнике данных не существует инструментов с тикерами %s.",
                        notExistedInstrument
                    )
            );
        }
        return tickers.stream().map(tickerToIdsMap::get).toList();
    }

    public List<Instrument> getUpdatableInstruments() {
        return instruments.stream().filter(Instrument::isUpdatable).toList();
    }

    public void enableUpdate(List<Ticker> tickers) {
        tickers.forEach(ticker -> findInstrumentBy(ticker).ifPresent(Instrument::enableUpdate));
    }

    public void disableUpdate(List<Ticker> tickers) {
        tickers.forEach(ticker -> findInstrumentBy(ticker).ifPresent(Instrument::disableUpdate));
    }

    private Map<Ticker, InstrumentId> getTickerToIdsMap() {
        Map<Ticker, InstrumentId> tickerToIdsMap = new HashMap<>();
        for (Instrument instrument : instruments) {
            tickerToIdsMap.put(instrument.getTicker(), instrument.getId());
        }
        return tickerToIdsMap;
    }

    private Optional<Instrument> findInstrumentBy(Ticker ticker) {
        return instruments.stream().filter(row -> row.getTicker().equals(ticker)).findFirst();
    }

    public void addInstrument(Instrument instrument) {
        Optional<Instrument> existed = findInstrumentBy(instrument.getTicker());
        if (existed.isEmpty()) {
            instruments.add(instrument);
        } else {
            existed.get().updateDetails(instrument.getDetails());
        }
    }
}
