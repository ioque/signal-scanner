package ru.ioque.acceptance.api.fixture;

import ru.ioque.acceptance.domain.dataemulator.core.InstrumentType;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPair;
import ru.ioque.acceptance.domain.dataemulator.futures.Futures;
import ru.ioque.acceptance.domain.dataemulator.index.Index;
import ru.ioque.acceptance.domain.dataemulator.stock.Stock;

import java.util.List;

public class InstrumentsFixture {
    public Index.IndexBuilder imoex() {
        return Index.builder()
            .secId("IMOEX")
            .boardId("SNDX")
            .name("Индекс МосБиржи")
            .shortName("Индекс МосБиржи")
            .annualHigh(3287.34)
            .annualLow(2126.4)
            .currencyId("RUB")
            .decimals(2)
            .type(InstrumentType.INDEX)
            .intradayValues(List.of())
            .historyValues(List.of());
    }

    public CurrencyPair.CurrencyPairBuilder usbRub() {
        return CurrencyPair.builder()
            .secId("USD000UTSTOM")
            .shortname("USDRUB_TOM")
            .secName("USDRUB_TOM - USD/РУБ")
            .lotSize(1000)
            .type(InstrumentType.CURRENCY)
            .intradayValues(List.of())
            .historyValues(List.of());
    }

    public Futures.FuturesBuilder brf4() {
        return Futures.builder()
            .secId("BRF4")
            .boardId("RFUD")
            .secName("Фьючерсный контракт BR-1.24")
            .shortname("BR-1.24")
            .type(InstrumentType.FUTURES)
            .intradayValues(List.of())
            .historyValues(List.of());
    }

    public Stock.StockBuilder sber() {
        return Stock.builder()
            .secId("SBER")
            .boardId("TQBR")
            .lotSize(100)
            .secName("Сбербанк")
            .shortname("Сбербанк")
            .type(InstrumentType.STOCK)
            .intradayValues(List.of())
            .historyValues(List.of());
    }

    public Stock.StockBuilder sberp() {
        return Stock.builder()
            .secId("SBERP")
            .boardId("TQBR")
            .lotSize(100)
            .secName("Сбербанк-п")
            .shortname("Сбербанк-п")
            .type(InstrumentType.STOCK)
            .intradayValues(List.of())
            .historyValues(List.of());
    }

    public Stock.StockBuilder sibn() {
        return Stock.builder()
            .secId("SIBN")
            .boardId("TQBR")
            .lotSize(100)
            .secName("Газпромнефть")
            .shortname("Газпромнефть")
            .type(InstrumentType.STOCK)
            .intradayValues(List.of())
            .historyValues(List.of());
    }

    public Stock.StockBuilder lkoh() {
        return Stock.builder()
            .secId("LKOH")
            .boardId("TQBR")
            .lotSize(100)
            .secName("Лукойл")
            .shortname("Лукойл")
            .type(InstrumentType.STOCK)
            .intradayValues(List.of())
            .historyValues(List.of());
    }

    public Stock.StockBuilder rosn() {
        return Stock.builder()
            .secId("ROSN")
            .boardId("TQBR")
            .lotSize(100)
            .secName("Роснефть")
            .shortname("Роснефть")
            .type(InstrumentType.STOCK)
            .intradayValues(List.of())
            .historyValues(List.of());
    }

    public Stock.StockBuilder tatn() {
        return Stock.builder()
            .secId("TATN")
            .boardId("TQBR")
            .lotSize(100)
            .secName("Татнефть")
            .shortname("Татнефть")
            .type(InstrumentType.STOCK)
            .intradayValues(List.of())
            .historyValues(List.of());
    }
}
