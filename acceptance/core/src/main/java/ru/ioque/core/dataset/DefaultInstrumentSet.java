package ru.ioque.core.dataset;

import ru.ioque.core.datagenerator.instrument.CurrencyPair;
import ru.ioque.core.datagenerator.instrument.Futures;
import ru.ioque.core.datagenerator.instrument.Index;
import ru.ioque.core.datagenerator.instrument.Stock;

public class DefaultInstrumentSet {
    public static Index imoex() {
        return Index.builder()
            .ticker("IMOEX")
            .name("Индекс фондового рынка мосбиржи")
            .shortName("Индекс фондового рынка мосбиржи")
            .annualHigh(3287.34)
            .annualLow(2126.4)
            .build();
    }

    public static CurrencyPair usbRub() {
        return CurrencyPair.builder()
            .ticker("USD000UTSTOM")
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .lotSize(1000)
            .faceUnit("RUB")
            .build();
    }

    public static Futures brf4() {
        return Futures.builder()
            .ticker("BRF4")
            .name("Фьючерсный контракт BR-1.24")
            .shortName("BR-1.24")
            .lotVolume(1000)
            .assetCode("BR")
            .build();
    }

    public static Stock sber() {
        return Stock.builder()
            .ticker("SBER")
            .lotSize(100)
            .name("ПАО Сбербанк")
            .shortName("Сбербанк")
            .build();
    }

    public static Stock sberp() {
        return Stock.builder()
            .ticker("SBERP")
            .lotSize(100)
            .name("Сбербанк-п")
            .shortName("Сбербанк-п")
            .build();
    }

    public static Stock sibn() {
        return Stock.builder()
            .ticker("SIBN")
            .lotSize(100)
            .name("Газпромнефть")
            .shortName("Газпромнефть")
            .build();
    }

    public static Stock lkoh() {
        return Stock.builder()
            .ticker("LKOH")
            .lotSize(100)
            .name("Лукойл")
            .shortName("Лукойл")
            .build();
    }

    public static Stock rosn() {
        return Stock.builder()
            .ticker("ROSN")
            .lotSize(100)
            .name("Роснефть")
            .shortName("Роснефть")
            .build();
    }

    public static Stock tatn() {
        return Stock.builder()
            .ticker("TATN")
            .lotSize(100)
            .name("Татнефть")
            .shortName("Татнефть")
            .build();
    }

    public static Stock tgkn() {
        return Stock.builder()
            .ticker("TGKN")
            .lotSize(100)
            .name("ТГК-14")
            .shortName("ТГК-14")
            .build();
    }
}
