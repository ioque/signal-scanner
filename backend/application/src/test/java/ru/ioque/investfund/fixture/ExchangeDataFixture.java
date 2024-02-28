package ru.ioque.investfund.fixture;

import lombok.Getter;
import lombok.SneakyThrows;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class ExchangeDataFixture {
    public final static Stock AFKS = Stock
        .builder()
        .id(UUID.randomUUID())
        .shortName("ао Система")
        .name("АФК \"Система\" ПАО ао")
        .ticker("AFKS")
        .lotSize(10000)
        .listLevel(1)
        .isin("isin")
        .regNumber("regNumber")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    public final static Stock NAUK = Stock
        .builder()
        .id(UUID.randomUUID())
        .shortName("iНПОНаука")
        .name("НПО Наука ао")
        .ticker("NAUK")
        .lotSize(100)
        .listLevel(3)
        .isin("isin")
        .regNumber("regNumber")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    public final static Stock NAUK_positive = Stock
        .builder()
        .id(UUID.randomUUID())
        .shortName("iНПОНаука ПОЗИТИВ")
        .name("НПО Наука ао ПОЗИТИВ")
        .ticker("NAUK_positive")
        .lotSize(100)
        .listLevel(3)
        .isin("isin")
        .regNumber("regNumber")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    public final static Stock NSVZ = Stock
        .builder()
        .id(UUID.randomUUID())
        .shortName("iНаукаСвяз")
        .name("Наука-Связь ПАО ао")
        .ticker("NSVZ")
        .lotSize(100)
        .listLevel(3)
        .isin("isin")
        .regNumber("regNumber")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    public final static Stock SBER = Stock
        .builder()
        .id(UUID.randomUUID())
        .shortName("Сбербанк")
        .name("Сбербанк России ПАО ао")
        .ticker("SBER")
        .lotSize(100)
        .listLevel(1)
        .isin("isin")
        .regNumber("regNumber")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    public final static Stock UNAC = Stock
        .builder()
        .id(UUID.randomUUID())
        .shortName("iАвиастКао")
        .name("Об.авиастр.корп. ПАО ао")
        .ticker("UNAC")
        .lotSize(100)
        .listLevel(3)
        .isin("isin")
        .regNumber("regNumber")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    //фьючерс на нефть
    public final static Futures BRF4 = Futures
        .builder()
        .id(UUID.randomUUID())
        .shortName("BR-1.24")
        .name("Фьючерсный контракт BR-1.24")
        .ticker("BRF4")
        .lotVolume(1000)
        .initialMargin(100D)
        .highLimit(100D)
        .lowLimit(100D)
        .assetCode("BR")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    //индекс мосбиржи
    public final static Index IMOEX = Index
        .builder()
        .id(UUID.randomUUID())
        .ticker("IMOEX")
        .shortName("Индекс МосБиржи")
        .name("Индекс МосБиржи")
        .annualHigh(100D)
        .annualLow(100D)
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    //фьючерс на курс доллара
    public final static Futures SiZ3 = Futures
        .builder()
        .id(UUID.randomUUID())
        .shortName("Si-12.23")
        .name("Фьючерсный контракт Si-12.23")
        .ticker("SiZ3")
        .lotVolume(1000)
        .initialMargin(100D)
        .highLimit(100D)
        .lowLimit(100D)
        .assetCode("Si")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();
    public final static CurrencyPair USD000UTSTOM = CurrencyPair
        .builder()
        .id(UUID.randomUUID())
        .shortName("USDRUB_TOM")
        .name("USDRUB_TOM - USD/РУБ")
        .ticker("USD000UTSTOM")
        .lotSize(1)
        .faceUnit("RUB")
        .dailyValues(new ArrayList<>())
        .intradayValues(new ArrayList<>())
        .build();

    @Getter
    private final List<IntradayValue> intradayValues = new ArrayList<>();
    @Getter
    private final List<DailyValue> dailyValues = new ArrayList<>();
    private final List<Instrument> instruments = new ArrayList<>(List.of(AFKS, NAUK, NAUK_positive, NSVZ, SBER, UNAC, IMOEX, SiZ3, BRF4, USD000UTSTOM));

    @SneakyThrows
    public List<DailyValue> getHistoryDataByTicker(String ticker) {
        return dailyValues.stream().filter(row -> row.getTicker().equals(ticker)).toList();
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

    public void initTradingResults(List<DailyValue> dailyValues) {
        this.dailyValues.clear();
        this.dailyValues.addAll(dailyValues);
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
            .dailyValues(futures.getDailyValues().stream().toList())
            .intradayValues(futures.getIntradayValues().stream().toList())
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
            .dailyValues(index.getDailyValues().stream().toList())
            .intradayValues(index.getIntradayValues().stream().toList())
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
            .dailyValues(currencyPair.getDailyValues().stream().toList())
            .intradayValues(currencyPair.getIntradayValues().stream().toList())
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
            .dailyValues(stock.getDailyValues().stream().toList())
            .intradayValues(stock.getIntradayValues().stream().toList())
            .build();
    }
}
