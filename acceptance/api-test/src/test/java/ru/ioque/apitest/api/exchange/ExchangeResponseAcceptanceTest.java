package ru.ioque.apitest.api.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.api.BaseApiAcceptanceTest;
import ru.ioque.core.datagenerator.config.DealsGeneratorConfig;
import ru.ioque.core.datagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.datagenerator.core.PercentageGrowths;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.intraday.Contract;
import ru.ioque.core.datagenerator.intraday.Deal;
import ru.ioque.core.datagenerator.intraday.Delta;
import ru.ioque.core.dto.exchange.response.ExchangeResponse;
import ru.ioque.core.dto.exchange.response.HistoryValueResponse;
import ru.ioque.core.dto.exchange.response.InstrumentInListResponse;
import ru.ioque.core.dto.exchange.response.InstrumentResponse;
import ru.ioque.core.dto.exchange.response.IntradayValueResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("МОДУЛЬ \"EXCHANGE\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ExchangeResponseAcceptanceTest extends BaseApiAcceptanceTest {
    @BeforeEach
    void initDateTime() {
        initDateTime(getDateTimeNow());
    }

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

        ExchangeResponse exchangeResponse = getExchange();
        List<InstrumentInListResponse> instruments = getInstruments();

        assertEquals("Московская Биржа", exchangeResponse.getName());
        assertEquals(
            "Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.",
            exchangeResponse.getDescription()
        );
        assertNotNull(exchangeResponse.getUrl());
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
        ExchangeResponse exchangeResponse = getExchange();
        List<InstrumentInListResponse> instruments = getInstruments();

        integrateInstruments(
            instruments().imoex().shortName("Индекс мосбиржи").build(),
            instruments().usbRub().build(),
            instruments().brf4().build(),
            instruments().sber().shortName("Сбербанкич").build()
        );
        ExchangeResponse updatedExchangeResponse = getExchange();
        List<InstrumentInListResponse> updatedInstruments = getInstruments();

        assertEquals(exchangeResponse.getName(), updatedExchangeResponse.getName());
        assertEquals(
            exchangeResponse.getDescription(),
            updatedExchangeResponse.getDescription()
        );
        assertEquals(exchangeResponse.getUrl(), updatedExchangeResponse.getUrl());
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

        List<InstrumentInListResponse> instruments = getInstruments(Map.of("ticker", "SBER"));

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

        List<InstrumentInListResponse> stocks = getInstruments(Map.of("type", "stock"));
        List<InstrumentInListResponse> currencyPairs = getInstruments(Map.of("type", "currencyPair"));
        List<InstrumentInListResponse> futures = getInstruments(Map.of("type", "futures"));
        List<InstrumentInListResponse> indexes = getInstruments(Map.of("type", "index"));

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

        List<InstrumentInListResponse> instruments = getInstruments(Map.of("shortname", "Сбер"));

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

        List<InstrumentInListResponse> instruments = getInstruments(Map.of("shortname", "BR", "type", "futures"));

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

        List<InstrumentInListResponse> instruments = getInstruments(Map.of("ticker", "IMOEX", "type", "index"));

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

        List<InstrumentInListResponse> instruments = getInstruments(Map.of(
            "shortname",
            "Сбер",
            "ticker",
            "SBER",
            "type",
            "stock"
        ));

        assertEquals(2, instruments.size());
    }

    @Test
    @DisplayName("""
        T9. Получение детализированной информации по финансовому инструменту.
        """)
    void testCase9() {
        integrateInstruments(instruments().sber().build());

        InstrumentResponse instrumentResponse = getInstrumentById(
            getInstruments()
                .stream()
                .filter(row -> row.getTicker().equals("SBER"))
                .findFirst()
                .map(InstrumentInListResponse::getId)
                .orElseThrow()
        );

        assertEquals("SBER", instrumentResponse.getTicker());
        assertEquals("Сбербанк", instrumentResponse.getShortName());
        assertEquals("ПАО Сбербанк", instrumentResponse.getName());
    }

    @Test
    @DisplayName("""
        T10. Включение обновления торговых данных по финансовым инструментам.
        """)
    void testCase10() {
        LocalDateTime time = getDateTimeNow();
        LocalDate startDate = time.toLocalDate().minusMonths(6);
        integrateInstruments(instruments().sber().build());
        datasetRepository().initIntradayValue(
            generator().generateDeals(
                DealsGeneratorConfig
                    .builder()
                    .ticker("SBER")
                    .numTrades(10)
                    .startPrice(10.)
                    .startValue(100D)
                    .date(time.toLocalDate())
                    .startTime(LocalTime.parse("10:00"))
                    .pricePercentageGrowths(List.of(new PercentageGrowths(9D, 1D)))
                    .valuePercentageGrowths(List.of(new PercentageGrowths(9D, 1D)))
                    .build()
            )
        );
        datasetRepository().initDailyResultValue(
            generator().generateHistory(
                HistoryGeneratorConfig
                    .builder()
                    .ticker("SBER")
                    .startClose(10.)
                    .startOpen(10.)
                    .startValue(1000D)
                    .days(180)
                    .startDate(startDate)
                    .openPricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                    .closePricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                    .valuePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                    .build()
            )
        );
        enableUpdateInstrumentBy(getInstrumentIds());
        integrateTradingData();

        InstrumentResponse sber = getInstrumentById(getInstrumentIds().get(0));
        assertEquals(130, sber.getHistoryValues().size());
        assertEquals(10, sber.getIntradayValues().size());
    }

    private static LocalDateTime getDateTimeNow() {
        return LocalDateTime.parse("2024-03-04T11:00:00");
    }

    @Test
    @DisplayName("""
        T11. Выключение обновления торговых данных по финансовым инструментам.
        """)
    void testCase11() {
        LocalDateTime time = getDateTimeNow();
        integrateInstruments(instruments().sber().build());
        datasetRepository().initIntradayValue(
            List.of(
                Deal
                    .builder()
                    .tradeNumber(1L)
                    .ticker("SBER")
                    .price(12.1)
                    .value(12.3)
                    .dateTime(time)
                    .build()
            )
        );
        datasetRepository().initDailyResultValue(
            List.of(
                HistoryValue
                    .builder()
                    .closePrice(3451.4)
                    .openPrice(3411.1)
                    .value(135132512351.1)
                    .ticker("SBER")
                    .tradeDate(time.toLocalDate().minusDays(1))
                    .build()
            )
        );
        List<UUID> ids = getInstrumentIds();
        disableUpdateInstrumentBy(ids);

        InstrumentResponse sber = getInstrumentById(ids.get(0));
        assertEquals(0, sber.getHistoryValues().size());
        assertEquals(0, sber.getIntradayValues().size());
    }

    @Test
    @DisplayName("""
        T12. Внутридневная интеграция торговых данных.
        """)
    void testCase12() {
        initInstrumentsWithTradingData();
        fullIntegrate();

        List<InstrumentResponse> instrumentResponses = getInstrumentIds().stream().map(this::getInstrumentById).toList();

        assertEquals(4, instrumentResponses.stream().filter(row -> !row.getHistoryValues().isEmpty()).toList().size());
        assertEquals(4, instrumentResponses.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
    }

    @Test
    @DisplayName("""
        T13. Очистка внутридневных данных.
        """)
    void testCase14() {
        initInstrumentsWithTradingData();
        fullIntegrate();
        clearIntradayValue();

        List<InstrumentResponse> instrumentResponses = getInstrumentIds().stream().map(this::getInstrumentById).toList();

        assertEquals(4, instrumentResponses.stream().filter(row -> !row.getHistoryValues().isEmpty()).toList().size());
        assertEquals(0, instrumentResponses.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
    }

    @Test
    @DisplayName("""
        T14. Перенос торговых данных в архив.
        """)
    void testCase15() {
        initInstrumentsWithTradingData();
        fullIntegrate();

        runArchiving();

        List<InstrumentResponse> instrumentResponses = getInstrumentIds().stream().map(this::getInstrumentById).toList();
        List<IntradayValueResponse> intradayValues = getIntradayValues(0, 4);
        List<HistoryValueResponse> dailyValues = getHistoryValues(0, 4);

        assertEquals(4, instrumentResponses.stream().filter(row -> !row.getHistoryValues().isEmpty()).toList().size());
        assertEquals(4, instrumentResponses.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
        assertEquals(4, intradayValues.size());
        assertEquals(4, dailyValues.size());
    }

    private void initInstrumentsWithTradingData() {
        LocalDateTime time = getDateTimeNow().minusMinutes(5);
        datasetRepository().initInstruments(
            List.of(
                instruments().imoex().build(),
                instruments().usbRub().build(),
                instruments().brf4().build(),
                instruments().sber().build(),
                instruments().imoex().build()
            )
        );
        datasetRepository().initDailyResultValue(
            List.of(
                HistoryValue
                    .builder()
                    .closePrice(3451.4)
                    .openPrice(3411.1)
                    .value(135132512351.1)
                    .ticker("USD000UTSTOM")
                    .tradeDate(time.toLocalDate().minusDays(1))
                    .build(),
                HistoryValue
                    .builder()
                    .closePrice(3451.4)
                    .openPrice(3411.1)
                    .value(135132512351.1)
                    .ticker("IMOEX")
                    .tradeDate(time.toLocalDate().minusDays(1))
                    .build(),
                HistoryValue
                    .builder()
                    .closePrice(3451.4)
                    .openPrice(3411.1)
                    .value(135132512351.1)
                    .ticker("BRF4")
                    .tradeDate(time.toLocalDate().minusDays(1))
                    .build(),
                HistoryValue
                    .builder()
                    .closePrice(3451.4)
                    .openPrice(3411.1)
                    .value(135132512351.1)
                    .ticker("SBER")
                    .tradeDate(time.toLocalDate().minusDays(1))
                    .build(),
                HistoryValue
                    .builder()
                    .closePrice(3451.4)
                    .openPrice(3411.1)
                    .value(135132512351.1)
                    .ticker("SBER")
                    .tradeDate(time.toLocalDate().minusDays(2))
                    .build(),
                HistoryValue
                    .builder()
                    .closePrice(3451.4)
                    .openPrice(3411.1)
                    .value(135132512351.1)
                    .ticker("SBER")
                    .tradeDate(time.toLocalDate().minusDays(3))
                    .build()
            )
        );
        datasetRepository()
            .initIntradayValue(
                List.of(
                    Delta
                        .builder()
                        .tradeNumber(1L)
                        .ticker("IMOEX")
                        .price(12.1)
                        .value(12.3)
                        .dateTime(time)
                        .build(),
                    Deal
                        .builder()
                        .tradeNumber(1L)
                        .ticker("USD000UTSTOM")
                        .price(12.1)
                        .value(12.3)
                        .dateTime(time)
                        .qnt(1)
                        .isBuy(true)
                        .build(),
                    Contract
                        .builder()
                        .tradeNumber(1L)
                        .ticker("BRF4")
                        .price(12.1)
                        .dateTime(time)
                        .value(100D)
                        .qnt(10)
                        .build(),
                    Deal
                        .builder()
                        .tradeNumber(1L)
                        .ticker("SBER")
                        .price(12.1)
                        .value(12.3)
                        .qnt(1)
                        .dateTime(time)
                        .isBuy(true)
                        .build(),
                    Deal
                        .builder()
                        .tradeNumber(1L)
                        .ticker("SBER")
                        .price(12.1)
                        .value(12.3)
                        .qnt(1)
                        .dateTime(time.plusMinutes(1))
                        .isBuy(true)
                        .build(),
                    Deal
                        .builder()
                        .tradeNumber(1L)
                        .ticker("SBER")
                        .price(12.1)
                        .value(12.3)
                        .qnt(1)
                        .dateTime(time.plusMinutes(2))
                        .isBuy(true)
                        .build()
                )
            );
    }
}
