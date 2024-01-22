package ru.ioque.acceptance.api.exchange.fixture;

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
}
