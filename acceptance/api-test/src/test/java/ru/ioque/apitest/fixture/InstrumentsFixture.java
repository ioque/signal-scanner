package ru.ioque.apitest.fixture;

import ru.ioque.core.datagenerator.instrument.CurrencyPair;
import ru.ioque.core.datagenerator.instrument.Futures;
import ru.ioque.core.datagenerator.instrument.Index;
import ru.ioque.core.datagenerator.instrument.Stock;

public class InstrumentsFixture {
    public Index.IndexBuilder imoex() {
        return Index.builder()
            .ticker("IMOEX")
            .name("Индекс фондового рынка мосбиржи")
            .shortName("Индекс фондового рынка мосбиржи")
            .annualHigh(3287.34)
            .annualLow(2126.4);
    }

    public CurrencyPair.CurrencyPairBuilder usbRub() {
        return CurrencyPair.builder()
            .ticker("USD000UTSTOM")
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .lotSize(1000)
            .faceUnit("RUB");
    }

    public Futures.FuturesBuilder brf4() {
        return Futures.builder()
            .ticker("BRF4")
            .name("Фьючерсный контракт BR-1.24")
            .shortName("BR-1.24")
            .lotVolume(1000)
            .assetCode("BR");
    }

    public Stock.StockBuilder sber() {
        return Stock.builder()
            .ticker("SBER")
            .lotSize(100)
            .name("ПАО Сбербанк")
            .shortName("Сбербанк");
    }

    public Stock.StockBuilder sberp() {
        return Stock.builder()
            .ticker("SBERP")
            .lotSize(100)
            .name("Сбербанк-п")
            .shortName("Сбербанк-п");
    }

    public Stock.StockBuilder sibn() {
        return Stock.builder()
            .ticker("SIBN")
            .lotSize(100)
            .name("Газпромнефть")
            .shortName("Газпромнефть");
    }

    public Stock.StockBuilder lkoh() {
        return Stock.builder()
            .ticker("LKOH")
            .lotSize(100)
            .name("Лукойл")
            .shortName("Лукойл");
    }

    public Stock.StockBuilder rosn() {
        return Stock.builder()
            .ticker("ROSN")
            .lotSize(100)
            .name("Роснефть")
            .shortName("Роснефть");
    }

    public Stock.StockBuilder tatn() {
        return Stock.builder()
            .ticker("TATN")
            .lotSize(100)
            .name("Татнефть")
            .shortName("Татнефть");
    }

    public Stock.StockBuilder tgkn() {
        return Stock.builder()
            .ticker("TGKN")
            .lotSize(100)
            .name("ТГК-14")
            .shortName("ТГК-14");
    }
}
