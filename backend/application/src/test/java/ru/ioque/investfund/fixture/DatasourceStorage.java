package ru.ioque.investfund.fixture;

import lombok.Getter;
import lombok.SneakyThrows;
import ru.ioque.investfund.domain.datasource.entity.CurrencyPair;
import ru.ioque.investfund.domain.datasource.entity.Futures;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.Stock;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DatasourceStorage {
    @Getter
    private final List<IntradayValue> intradayValues = new ArrayList<>();
    @Getter
    private final List<HistoryValue> historyValues = new ArrayList<>();
    private final List<Instrument> instruments = new ArrayList<>();

    @SneakyThrows
    public List<HistoryValue> getHistoryDataByTicker(String ticker) {
        return historyValues.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    @SneakyThrows
    public List<IntradayValue> getDealsByTicker(String ticker) {
        return intradayValues.stream().filter(row -> row.getTicker().equals(ticker)).toList();
    }

    public void initInstruments(List<Instrument> instruments) {
        this.instruments.clear();
        this.instruments.addAll(instruments);
    }

    public void initDealDatas(List<IntradayValue> intradayValues) {
        this.intradayValues.clear();
        this.intradayValues.addAll(intradayValues);
    }

    public void initTradingResults(List<HistoryValue> historyValues) {
        this.historyValues.clear();
        this.historyValues.addAll(historyValues);
    }

    public List<Instrument> getInstruments() {
        return instruments.stream().map(row -> copyMethods.get(row.getClass()).apply(row)).toList();
    }

    Map<Class<? extends Instrument>, Function<Instrument, Instrument>> copyMethods = Map.of(
        Stock.class, this::copyStock,
        CurrencyPair.class, this::copyCurrencyPair,
        Index.class, this::copyIndex,
        Futures.class, this::copyFutures
    );

    private Instrument copyFutures(Instrument instrument) {
        Futures futures = (Futures) instrument;
        return Futures.builder()
            .id(futures.getId())
            .ticker(futures.getTicker())
            .name(futures.getName())
            .shortName(futures.getShortName())
            .assetCode(futures.getAssetCode())
            .initialMargin(futures.getInitialMargin())
            .lowLimit(futures.getLowLimit())
            .highLimit(futures.getHighLimit())
            .lotVolume(futures.getLotVolume())
            .build();
    }

    private Instrument copyIndex(Instrument instrument) {
        Index index = (Index) instrument;
        return Index.builder()
            .id(index.getId())
            .ticker(index.getTicker())
            .name(index.getName())
            .shortName(index.getShortName())
            .annualHigh(index.getAnnualHigh())
            .annualLow(index.getAnnualLow())
            .build();
    }

    private Instrument copyCurrencyPair(Instrument instrument) {
        CurrencyPair currencyPair = (CurrencyPair) instrument;
        return CurrencyPair.builder()
            .id(currencyPair.getId())
            .ticker(currencyPair.getTicker())
            .name(currencyPair.getName())
            .shortName(currencyPair.getShortName())
            .lotSize(currencyPair.getLotSize())
            .faceUnit(currencyPair.getFaceUnit())
            .build();
    }

    private Instrument copyStock(Instrument instrument) {
        Stock stock = (Stock) instrument;
        return Stock.builder()
            .id(stock.getId())
            .ticker(stock.getTicker())
            .name(stock.getName())
            .shortName(stock.getShortName())
            .isin(stock.getIsin())
            .regNumber(stock.getRegNumber())
            .listLevel(stock.getListLevel())
            .build();
    }
}
