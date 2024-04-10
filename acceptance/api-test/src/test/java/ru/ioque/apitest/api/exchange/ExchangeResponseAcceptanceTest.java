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
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dataset.DefaultInstrumentSet;
import ru.ioque.core.dto.exchange.request.RegisterDatasourceRequest;
import ru.ioque.core.dto.exchange.response.ExchangeResponse;
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
        registerDatasource(
            RegisterDatasourceRequest.builder()
                .name("Московская Биржа")
                .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
                .url(datasourceHost)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T1. Интеграция с Биржей.
        """)
    void testCase1() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        ExchangeResponse exchangeResponse = getExchangeBy(datasourceId);
        List<InstrumentInListResponse> instruments = getInstruments(datasourceId);
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
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);
        ExchangeResponse exchangeResponse = getExchangeBy(datasourceId);
        List<InstrumentInListResponse> instruments = getInstruments(datasourceId);

        integrateInstruments(datasourceId);
        ExchangeResponse updatedExchangeResponse = getExchangeBy(datasourceId);
        List<InstrumentInListResponse> updatedInstruments = getInstruments(datasourceId);

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
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(2, getInstruments(datasourceId, Map.of("ticker", "SBER")).size());
    }

    @Test
    @DisplayName("""
        T4. Поиск финансовых инструментов по типу.
        """)
    void testCase4() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(2, getInstruments(datasourceId, Map.of("type", "stock")).size());
        assertEquals(1, getInstruments(datasourceId, Map.of("type", "currencyPair")).size());
        assertEquals(1, getInstruments(datasourceId, Map.of("type", "futures")).size());
        assertEquals(1, getInstruments(datasourceId, Map.of("type", "index")).size());
    }

    @Test
    @DisplayName("""
        T5. Поиск финансовых инструментов по названию.
        """)
    void testCase5() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(2, getInstruments(datasourceId, Map.of("shortname", "Сбер")).size());
    }

    @Test
    @DisplayName("""
        T6. Поиск финансовых инструментов по названию и типу.
        """)
    void testCase6() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(1, getInstruments(datasourceId, Map.of("shortname", "BR", "type", "futures")).size());
    }

    @Test
    @DisplayName("""
        T7. Поиск финансовых инструментов по тикеру и типу.
        """)
    void testCase7() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(1, getInstruments(datasourceId, Map.of("ticker", "IMOEX", "type", "index")).size());
    }

    @Test
    @DisplayName("""
        T8. Поиск финансовых инструментов по тикеру, названию и типу.
        """)
    void testCase8() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(
            2,
            getInstruments(datasourceId, Map.of("shortname", "Сбер", "ticker", "SBER", "type", "stock")).size()
        );
    }

    @Test
    @DisplayName("""
        T9. Получение детализированной информации по финансовому инструменту.
        """)
    void testCase9() {
        initDataset(Dataset.builder().instruments(List.of(DefaultInstrumentSet.sber())).build());
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        InstrumentResponse instrumentResponse = getInstrumentBy(datasourceId, "SBER");

        assertEquals("SBER", instrumentResponse.getTicker());
        assertEquals("Сбербанк", instrumentResponse.getShortName());
        assertEquals("ПАО Сбербанк", instrumentResponse.getName());
    }

    @Test
    @DisplayName("""
        T10. Включение обновления торговых данных по финансовым инструментам.
        """)
    void testCase10() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        LocalDateTime time = getDateTimeNow();
        LocalDate startDate = time.toLocalDate().minusMonths(6);
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.sber()))
                .intradayValues(
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
                )
                .historyValues(
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
                )
                .build()
        );
        integrateInstruments(datasourceId);

        integrateTradingData(datasourceId);

        InstrumentResponse sber = getInstrumentBy(datasourceId, "SBER");
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
        UUID datasourceId = getAllDatasource().get(0).getId();
        LocalDateTime time = getDateTimeNow();
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.sber()))
                .intradayValues(
                    List.of(
                        Deal
                            .builder()
                            .number(1L)
                            .ticker("SBER")
                            .price(12.1)
                            .value(12.3)
                            .dateTime(time)
                            .build()
                    )
                )
                .historyValues(
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
                )
                .build()
        );
        integrateInstruments(datasourceId);

        disableUpdateInstrumentBy(datasourceId, List.of("SBER"));
        integrateTradingData(datasourceId);

        InstrumentResponse sber = getInstrumentBy(datasourceId, "SBER");
        assertEquals(0, sber.getHistoryValues().size());
        assertEquals(0, sber.getIntradayValues().size());
    }

    @Test
    @DisplayName("""
        T12. Внутридневная интеграция торговых данных.
        """)
    void testCase12() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        initInstrumentsWithTradingData();
        fullIntegrate(datasourceId);

        List<InstrumentResponse> instrumentResponses = getTickers(datasourceId)
            .stream()
            .map(ticker -> getInstrumentBy(datasourceId, ticker))
            .toList();

        assertEquals(4, instrumentResponses.stream().filter(row -> !row.getHistoryValues().isEmpty()).toList().size());
        assertEquals(4, instrumentResponses.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
    }

    @Test
    @DisplayName("""
        T13. Перенос внутридневных данных в архив.
        """)
    void testCase15() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        initInstrumentsWithTradingData();
        fullIntegrate(datasourceId);

        runArchiving();

        List<InstrumentResponse> instrumentResponses = getTickers(datasourceId)
            .stream()
            .map(ticker -> getInstrumentBy(datasourceId, ticker))
            .toList();
        List<IntradayValueResponse> intradayValues = getIntradayValues(0, 4);
        assertEquals(0, instrumentResponses.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
        assertEquals(4, intradayValues.size());
    }

    private void initInstrumentsWithTradingData() {
        LocalDateTime time = getDateTimeNow().minusMinutes(5);
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.imoex()
                    )
                )
                .intradayValues(
                    List.of(
                        Delta
                            .builder()
                            .number(1L)
                            .ticker("IMOEX")
                            .price(12.1)
                            .value(12.3)
                            .dateTime(time)
                            .build(),
                        Deal
                            .builder()
                            .number(1L)
                            .ticker("USD000UTSTOM")
                            .price(12.1)
                            .value(12.3)
                            .dateTime(time)
                            .qnt(1)
                            .isBuy(true)
                            .build(),
                        Contract
                            .builder()
                            .number(1L)
                            .ticker("BRF4")
                            .price(12.1)
                            .dateTime(time)
                            .value(100D)
                            .qnt(10)
                            .build(),
                        Deal
                            .builder()
                            .number(1L)
                            .ticker("SBER")
                            .price(12.1)
                            .value(12.3)
                            .qnt(1)
                            .dateTime(time)
                            .isBuy(true)
                            .build(),
                        Deal
                            .builder()
                            .number(2L)
                            .ticker("SBER")
                            .price(12.1)
                            .value(12.3)
                            .qnt(1)
                            .dateTime(time.plusMinutes(1))
                            .isBuy(true)
                            .build(),
                        Deal
                            .builder()
                            .number(3L)
                            .ticker("SBER")
                            .price(12.1)
                            .value(12.3)
                            .qnt(1)
                            .dateTime(time.plusMinutes(2))
                            .isBuy(true)
                            .build()
                    )
                )
                .historyValues(
                    List.of(
                        HistoryValue
                            .builder()
                            .closePrice(3451.4)
                            .openPrice(3411.1)
                            .lowPrice(3451.1)
                            .highPrice(3311.1)
                            .value(135132512351.1)
                            .ticker("USD000UTSTOM")
                            .tradeDate(time.toLocalDate().minusDays(1))
                            .build(),
                        HistoryValue
                            .builder()
                            .closePrice(3451.4)
                            .openPrice(3411.1)
                            .lowPrice(3451.1)
                            .highPrice(3311.1)
                            .value(135132512351.1)
                            .ticker("IMOEX")
                            .tradeDate(time.toLocalDate().minusDays(1))
                            .build(),
                        HistoryValue
                            .builder()
                            .closePrice(3451.4)
                            .openPrice(3411.1)
                            .lowPrice(3451.1)
                            .highPrice(3311.1)
                            .value(135132512351.1)
                            .ticker("BRF4")
                            .tradeDate(time.toLocalDate().minusDays(1))
                            .build(),
                        HistoryValue
                            .builder()
                            .closePrice(3451.4)
                            .openPrice(3411.1)
                            .lowPrice(3451.1)
                            .highPrice(3311.1)
                            .value(135132512351.1)
                            .ticker("SBER")
                            .tradeDate(time.toLocalDate().minusDays(1))
                            .build(),
                        HistoryValue
                            .builder()
                            .closePrice(3451.4)
                            .openPrice(3411.1)
                            .lowPrice(3451.1)
                            .highPrice(3311.1)
                            .value(135132512351.1)
                            .ticker("SBER")
                            .tradeDate(time.toLocalDate().minusDays(2))
                            .build(),
                        HistoryValue
                            .builder()
                            .closePrice(3451.4)
                            .openPrice(3411.1)
                            .lowPrice(3451.1)
                            .highPrice(3311.1)
                            .value(135132512351.1)
                            .ticker("SBER")
                            .tradeDate(time.toLocalDate().minusDays(3))
                            .build()
                    )
                )
                .build()
        );
    }
}
