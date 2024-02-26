package ru.ioque.acceptance.api.exchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.adapters.client.testingsystem.response.DailyValueResponse;
import ru.ioque.acceptance.adapters.client.testingsystem.response.IntradayValueResponse;
import ru.ioque.acceptance.api.BaseApiAcceptanceTest;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairDailyResult;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairTrade;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesDailyResult;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesTrade;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDailyResult;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDelta;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;
import ru.ioque.acceptance.domain.exchange.Exchange;
import ru.ioque.acceptance.domain.exchange.Instrument;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;
import ru.ioque.acceptance.domain.exchange.InstrumentStatistic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("МОДУЛЬ \"EXCHANGE\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ExchangeAcceptanceTest extends BaseApiAcceptanceTest {
    @Test
    @DisplayName("""
        T1. Интеграция с Биржей.
        """)
    void testCase1() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build()
        );

        Exchange exchange = getExchange();
        List<InstrumentInList> instruments = getInstruments();

        assertEquals("Московская Биржа", exchange.getName());
        assertEquals(
            "Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.",
            exchange.getDescription()
        );
        assertEquals("http://localhost:8081", exchange.getUrl());
        assertEquals(4, instruments.size());
    }

    @Test
    @DisplayName("""
        T2. Повторная синхронизация с источником биржевых данных.
        """)
    void testCase2() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build()
        );
        Exchange exchange = getExchange();
        List<InstrumentInList> instruments = getInstruments();

        integrateInstruments(
            instruments().imoex().shortName("Индекс мосбиржи").build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().shortname("Сбербанкич").build()
        );
        Exchange updatedExchange = getExchange();
        List<InstrumentInList> updatedInstruments = getInstruments();

        assertEquals(exchange.getName(), updatedExchange.getName());
        assertEquals(
            exchange.getDescription(),
            updatedExchange.getDescription()
        );
        assertEquals(exchange.getUrl(), updatedExchange.getUrl());
        assertEquals(instruments.size(), updatedInstruments.size());
        assertEquals(
            "Индекс фондового рынка мосбиржи",
            updatedInstruments
                .stream()
                .filter(row -> row.getTicker().equals("IMOEX"))
                .findFirst()
                .orElseThrow()
                .getShortName()
        );
        assertEquals(
            "Сбербанк",
            updatedInstruments
                .stream()
                .filter(row -> row.getTicker().equals("SBER"))
                .findFirst()
                .orElseThrow()
                .getShortName()
        );
    }

    @Test
    @DisplayName("""
        T3. Поиск финансовых инструментов по тикеру.
        """)
    void testCase3() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build(),
            instruments().sberp().build()
        );

        List<InstrumentInList> instruments = getInstruments(Map.of("ticker", "SBER"));

        assertEquals(2, instruments.size());
    }

    @Test
    @DisplayName("""
        T4. Поиск финансовых инструментов по типу.
        """)
    void testCase4() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build(),
            instruments().sberp().build()
        );

        List<InstrumentInList> stocks = getInstruments(Map.of("type", "stock"));
        List<InstrumentInList> currencyPairs = getInstruments(Map.of("type", "currencyPair"));
        List<InstrumentInList> futures = getInstruments(Map.of("type", "futures"));
        List<InstrumentInList> indexes = getInstruments(Map.of("type", "index"));

        assertEquals(2, stocks.size());
        assertEquals(1, currencyPairs.size());
        assertEquals(1, futures.size());
        assertEquals(1, indexes.size());
    }

    @Test
    @DisplayName("""
        T5. Поиск финансовых инструментов по названию.
        """)
    void testCase5() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build(),
            instruments().sberp().build()
        );

        List<InstrumentInList> instruments = getInstruments(Map.of("shortname", "Сбер"));

        assertEquals(2, instruments.size());
    }

    @Test
    @DisplayName("""
        T6. Поиск финансовых инструментов по названию и типу.
        """)
    void testCase6() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build(),
            instruments().sberp().build()
        );

        List<InstrumentInList> instruments = getInstruments(Map.of("shortname", "BR", "type", "futures"));

        assertEquals(1, instruments.size());
    }

    @Test
    @DisplayName("""
        T7. Поиск финансовых инструментов по тикеру и типу.
        """)
    void testCase7() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build(),
            instruments().sberp().build()
        );

        List<InstrumentInList> instruments = getInstruments(Map.of("ticker", "IMOEX", "type", "index"));

        assertEquals(1, instruments.size());
    }

    @Test
    @DisplayName("""
        T8. Поиск финансовых инструментов по тикеру, названию и типу.
        """)
    void testCase8() {
        integrateInstruments(
            instruments().imoex().build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().build(),
            instruments().sberp().build()
        );

        List<InstrumentInList> instruments = getInstruments(Map.of("shortname", "Сбер", "ticker", "SBER", "type", "stock"));

        assertEquals(2, instruments.size());
    }

    @Test
    @DisplayName("""
        T9. Получение детализированной информации по финансовому инструменту.
        """)
    void testCase9() {
        integrateInstruments(instruments().sber().build());

        Instrument instrument = getInstrumentById(
            getInstruments()
                .stream()
                .filter(row -> row.getTicker().equals("SBER"))
                .findFirst()
                .map(InstrumentInList::getId)
                .orElseThrow()
        );

        assertEquals("SBER", instrument.getTicker());
        assertEquals("Сбербанк", instrument.getShortName());
        assertEquals("ПАО Сбербанк", instrument.getName());
    }

    @Test
    @DisplayName("""
        T10. Включение обновления торговых данных по финансовым инструментам.
        """)
    void testCase10() {
        LocalDateTime time = LocalDateTime.now();
        integrateInstruments(instruments()
            .sber()
            .historyValues(
                List.of(
                    StockDailyResult
                        .builder()
                        .close(3451.4)
                        .open(3411.1)
                        .value(135132512351.1)
                        .volume(123124124.2)
                        .secId("SBER")
                        .tradeDate(time.toLocalDate().minusDays(1))
                        .build()
                )
            )
            .intradayValues(
                List.of(
                    StockTrade
                        .builder()
                        .tradeNo(1)
                        .secId("SBER")
                        .price(12.1)
                        .value(12.3)
                        .tradeTime(time.toLocalTime())
                        .sysTime(time)
                        .build()
                )
            )
            .build());

        enableUpdateInstrumentBy(getInstrumentIds());
        integrateTradingData();

        Instrument sber = getInstrumentById(getInstrumentIds().get(0));
        assertEquals(1, sber.getDailyValues().size());
        assertEquals(1, sber.getIntradayValues().size());
    }

    @Test
    @DisplayName("""
        T11. Выключение обновления торговых данных по финансовым инструментам.
        """)
    void testCase11() {
        LocalDateTime time = LocalDateTime.now();
        integrateInstruments(instruments().sber()
            .historyValues(
                List.of(
                    StockDailyResult
                        .builder()
                        .close(3451.4)
                        .open(3411.1)
                        .value(135132512351.1)
                        .volume(123124124.2)
                        .secId("SBER")
                        .tradeDate(time.toLocalDate().minusDays(1))
                        .build()
                )
            )
            .intradayValues(
                List.of(
                    StockTrade
                        .builder()
                        .tradeNo(1)
                        .secId("SBER")
                        .price(12.1)
                        .value(12.3)
                        .tradeTime(time.toLocalTime())
                        .sysTime(time)
                        .build()
                )
            )
            .build());
        List<UUID> ids = getInstrumentIds();
        disableUpdateInstrumentBy(ids);

        Instrument sber = getInstrumentById(ids.get(0));
        assertEquals(0, sber.getDailyValues().size());
        assertEquals(0, sber.getIntradayValues().size());
    }

    @Test
    @DisplayName("""
        T12. Внутридневная интеграция торговых данных.
        """)
    void testCase12() {
        initInstrumentsWithTradingData();
        fullIntegrate();

        List<Instrument> instruments = getInstrumentIds().stream().map(this::getInstrumentById).toList();

        assertEquals(4, instruments.stream().filter(row -> !row.getDailyValues().isEmpty()).toList().size());
        assertEquals(4, instruments.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
    }

    @Test
    @DisplayName("""
        T13. Получение статистики инструмента.
        """)
    void testCase13() {
        initInstrumentsWithTradingData();
        fullIntegrate();

        InstrumentStatistic instrumentStatistic = getInstrumentStatisticBy("SBER");

        assertEquals(37, Math.round(instrumentStatistic.getTodayValue()));
    }

    @Test
    @DisplayName("""
        T14. Очистка внутридневных данных.
        """)
    void testCase14() {
        initInstrumentsWithTradingData();
        fullIntegrate();
        clearIntradayValue();

        List<Instrument> instruments = getInstrumentIds().stream().map(this::getInstrumentById).toList();

        assertEquals(4, instruments.stream().filter(row -> !row.getDailyValues().isEmpty()).toList().size());
        assertEquals(0, instruments.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
    }

    @Test
    @DisplayName(
        """
        T15. Перенос данных в хранилище системы тестирования.
        """)
    void testCase15() {
        initInstrumentsWithTradingData();
        fullIntegrate();

        runDataTransfer();

        List<Instrument> instruments = getInstrumentIds().stream().map(this::getInstrumentById).toList();
        List<IntradayValueResponse> intradayValues = getIntradayValues(0, 4);
        List<DailyValueResponse> dailyValues = getDailyValues(0, 4);

        assertEquals(4, instruments.stream().filter(row -> !row.getDailyValues().isEmpty()).toList().size());
        assertEquals(4, instruments.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
        assertEquals(4, intradayValues.size());
        assertEquals(4, dailyValues.size());
    }

    private void initInstrumentsWithTradingData() {
        LocalDateTime time = LocalDateTime.now().minusMinutes(5);
        datasetManager()
            .initDataset(
                List.of(
                    instruments()
                        .imoex()
                        .historyValues(
                            List.of(
                                IndexDailyResult
                                    .builder()
                                    .close(3451.4)
                                    .open(3411.1)
                                    .value(135132512351.1)
                                    .volume(123124124.2)
                                    .secId("IMOEX")
                                    .tradeDate(time.toLocalDate().minusDays(1))
                                    .build()
                            )
                        )
                        .intradayValues(
                            List.of(
                                IndexDelta
                                    .builder()
                                    .tradeNo(1)
                                    .secId("IMOEX")
                                    .price(12.1)
                                    .value(12.3)
                                    .tradeTime(time.toLocalTime())
                                    .tradeDate(time.toLocalDate())
                                    .sysTime(time)
                                    .build()
                            )
                        )
                        .build(),
                    instruments().usbRub()
                        .historyValues(
                            List.of(
                                CurrencyPairDailyResult
                                    .builder()
                                    .close(3451.4)
                                    .open(3411.1)
                                    .volRur(135132512351.1)
                                    .numTrades(1000D)
                                    .secId("USD000UTSTOM")
                                    .tradeDate(time.toLocalDate().minusDays(1))
                                    .build()
                            )
                        )
                        .intradayValues(
                            List.of(
                                CurrencyPairTrade
                                    .builder()
                                    .tradeNo(1)
                                    .secId("USD000UTSTOM")
                                    .price(12.1)
                                    .value(12.3)
                                    .tradeTime(time.toLocalTime())
                                    .sysTime(time)
                                    .build()
                            )
                        )
                        .build(),
                    instruments().brf4()
                        .historyValues(
                            List.of(
                                FuturesDailyResult
                                    .builder()
                                    .close(3451.4)
                                    .open(3411.1)
                                    .value(135132512351.1)
                                    .volume(123124124)
                                    .secId("BRF4")
                                    .tradeDate(time.toLocalDate().minusDays(1))
                                    .build()
                            )
                        )
                        .intradayValues(
                            List.of(
                                FuturesTrade
                                    .builder()
                                    .tradeNo(1)
                                    .secId("BRF4")
                                    .price(12.1)
                                    .tradeTime(time.toLocalTime())
                                    .tradeDate(time.toLocalDate())
                                    .sysTime(time)
                                    .build()
                            )
                        )
                        .build(),
                    instruments().sber()
                        .historyValues(
                            List.of(
                                StockDailyResult
                                    .builder()
                                    .close(3451.4)
                                    .open(3411.1)
                                    .value(135132512351.1)
                                    .volume(123124124.2)
                                    .secId("SBER")
                                    .tradeDate(time.toLocalDate().minusDays(1))
                                    .build(),
                                StockDailyResult
                                    .builder()
                                    .close(3451.4)
                                    .open(3411.1)
                                    .value(135132512351.1)
                                    .volume(123124124.2)
                                    .secId("SBER")
                                    .tradeDate(time.toLocalDate().minusDays(2))
                                    .build(),
                                StockDailyResult
                                    .builder()
                                    .close(3451.4)
                                    .open(3411.1)
                                    .value(135132512351.1)
                                    .volume(123124124.2)
                                    .secId("SBER")
                                    .tradeDate(time.toLocalDate().minusDays(3))
                                    .build()
                            )
                        )
                        .intradayValues(
                            List.of(
                                StockTrade
                                    .builder()
                                    .tradeNo(1)
                                    .secId("SBER")
                                    .price(12.1)
                                    .value(12.3)
                                    .tradeTime(time.toLocalTime())
                                    .sysTime(time)
                                    .build(),
                                StockTrade
                                    .builder()
                                    .tradeNo(1)
                                    .secId("SBER")
                                    .price(12.1)
                                    .value(12.3)
                                    .tradeTime(time.plusMinutes(1).toLocalTime())
                                    .sysTime(time.plusMinutes(1))
                                    .build(),
                                StockTrade
                                    .builder()
                                    .tradeNo(1)
                                    .secId("SBER")
                                    .price(12.1)
                                    .value(12.3)
                                    .tradeTime(time.plusMinutes(2).toLocalTime())
                                    .sysTime(time.plusMinutes(2))
                                    .build()
                            )
                        )
                        .build()
                )
            );
    }
}
