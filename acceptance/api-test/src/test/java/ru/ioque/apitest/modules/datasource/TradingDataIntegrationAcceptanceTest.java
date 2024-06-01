package ru.ioque.apitest.modules.datasource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.Contract;
import ru.ioque.core.datagenerator.intraday.Deal;
import ru.ioque.core.datagenerator.intraday.Delta;
import ru.ioque.core.datagenerator.intraday.IntradayValue;
import ru.ioque.core.dataset.DefaultInstrumentSet;
import ru.ioque.core.dto.datasource.request.DatasourceRequest;
import ru.ioque.core.dto.datasource.response.InstrumentResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TRADING DATA INTEGRATION ACCEPTANCE TEST")
public class TradingDataIntegrationAcceptanceTest extends DatasourceAcceptanceTest {
    @BeforeEach
    void initDateTime() {
        initDateTime(getDateTimeNow());
        createDatasource(
            DatasourceRequest.builder()
                .name("Московская Биржа")
                .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
                .url(datasourceHost)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T1. Включение обновления торговых данных по финансовым инструментам.
        """)
    void testCase1() {
        final UUID datasourceId = getFirstDatasourceId();
        final List<Instrument> instrumentList = getInstrumentList();
        initInstruments(instrumentList);
        synchronizeDatasource(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));

        instrumentList.forEach(dto -> {
            InstrumentResponse instrument = getInstrumentBy(datasourceId, dto.getTicker());
            assertTrue(instrument.getUpdatable());
        });
    }

    @Test
    @DisplayName("""
        T2. Выключение обновления торговых данных по финансовым инструментам.
        """)
    void testCase11() {
        final UUID datasourceId = getFirstDatasourceId();
        final List<Instrument> instrumentList = getInstrumentList();
        initInstruments(instrumentList);

        prepareDatasource(datasourceId);
        disableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));

        instrumentList.forEach(dto -> {
            InstrumentResponse instrument = getInstrumentBy(datasourceId, dto.getTicker());
            assertFalse(instrument.getUpdatable());
        });
    }

    @Test
    @DisplayName("""
        T3. Интеграция агрегированных итогов торговых дней.
        """)
    void testCase3() {
        final UUID datasourceId = getFirstDatasourceId();
        final List<Instrument> instrumentList = getInstrumentList();
        final List<HistoryValue> historyValues = getHistoryValues();
        initDataset(instrumentList, historyValues, List.of());

        prepareDatasource(datasourceId);

        instrumentList.forEach(dto -> {
            InstrumentResponse instrumentResponse = getInstrumentBy(datasourceId, dto.getTicker());
            assertEquals(4, instrumentResponse.getHistoryValues().size());
        });
    }

    @Test
    @DisplayName("""
        T4. Интеграция внутридневных данных.
        """)
    void testCase4() {
        final UUID datasourceId = getFirstDatasourceId();
        final List<Instrument> instrumentList = getInstrumentList();
        final List<IntradayValue> intradayValues = getIntradayValues();
        initDataset(instrumentList, List.of(), intradayValues);

        prepareDatasource(datasourceId);
        publishIntradayData(datasourceId);

        instrumentList.forEach(dto -> {
            InstrumentResponse instrumentResponse = getInstrumentBy(datasourceId, dto.getTicker());
            assertNotNull(instrumentResponse.getTodayValue());
            assertNotNull(instrumentResponse.getTodayFirstPrice());
            assertNotNull(instrumentResponse.getTodayLastPrice());
        });
    }

    @Test
    @DisplayName("""
        T5. Интеграция исторических данных, ранее уже были загружены исторические данные за предпредыдущий день.
        """)
    void testCase5() {
        initDateTime(LocalDateTime.parse("2024-03-21T13:00:00"));
        final UUID datasourceId = getFirstDatasourceId();
        final List<Instrument> instrumentList = getInstrumentList();
        initDataset(instrumentList, getHistoryValues(), List.of());
        prepareDatasource(datasourceId);
        instrumentList.forEach(dto -> {
            InstrumentResponse instrumentResponse = getInstrumentBy(datasourceId, dto.getTicker());
            assertEquals(3, instrumentResponse.getHistoryValues().size());
        });

        initDateTime(LocalDateTime.parse("2024-03-22T13:00:00"));
        prepareDatasource(datasourceId);
        instrumentList.forEach(dto -> {
            InstrumentResponse instrumentResponse = getInstrumentBy(datasourceId, dto.getTicker());
            assertEquals(4, instrumentResponse.getHistoryValues().size());
        });
    }

    @Test
    @DisplayName("""
        T6. Номер последней загруженной сделки 1. Интегрируются сделки с большим номером.
        """)
    void testCase6() {
        final UUID datasourceId = getFirstDatasourceId();
        final List<Instrument> instrumentList = List.of(DefaultInstrumentSet.sber());
        final List<IntradayValue> intradayValues = List.of(Deal.builder().number(1L).ticker("SBER").qnt(100).isBuy(true).price(270D).value(270000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build());
        initDataset(instrumentList, List.of(), intradayValues);
        prepareDatasource(datasourceId);
        publishIntradayData(datasourceId);
        assertEquals(1, getIntradayValues(0, 10).size());
        initDataset(instrumentList, List.of(), getIntradayValues());
        publishIntradayData(datasourceId);
        assertEquals(4, getIntradayValues(0, 10).size());
    }

    private LocalDateTime getDateTimeNow() {
        return LocalDateTime.parse("2024-03-22T13:00:00");
    }

    private List<Instrument> getInstrumentList() {
        return List.of(
            DefaultInstrumentSet.imoex(),
            DefaultInstrumentSet.usbRub(),
            DefaultInstrumentSet.brf4(),
            DefaultInstrumentSet.sber()
        );
    }

    private List<HistoryValue> getHistoryValues() {
        List<HistoryValue> imoexHistory = List.of(
            HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3050D).lowPrice(2700D).openPrice(2800D).closePrice(3000D).tradeDate(LocalDate.parse("2024-03-18")).build(),
            HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3150D).lowPrice(2800D).openPrice(2900D).closePrice(3100D).tradeDate(LocalDate.parse("2024-03-19")).build(),
            HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3250D).lowPrice(2900D).openPrice(3000D).closePrice(3200D).tradeDate(LocalDate.parse("2024-03-20")).build(),
            HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3350D).lowPrice(3000D).openPrice(3100D).closePrice(3300D).tradeDate(LocalDate.parse("2024-03-21")).build()
        );
        List<HistoryValue> usdRubHistory = List.of(
            HistoryValue.builder().ticker("USD000UTSTOM").value(300_000_000D).highPrice(80D).lowPrice(70D).openPrice(72D).closePrice(76D).tradeDate(LocalDate.parse("2024-03-18")).build(),
            HistoryValue.builder().ticker("USD000UTSTOM").value(300_000_000D).highPrice(81D).lowPrice(71D).openPrice(73D).closePrice(77D).tradeDate(LocalDate.parse("2024-03-19")).build(),
            HistoryValue.builder().ticker("USD000UTSTOM").value(300_000_000D).highPrice(82D).lowPrice(72D).openPrice(74D).closePrice(78D).tradeDate(LocalDate.parse("2024-03-20")).build(),
            HistoryValue.builder().ticker("USD000UTSTOM").value(300_000_000D).highPrice(83D).lowPrice(73D).openPrice(75D).closePrice(79D).tradeDate(LocalDate.parse("2024-03-21")).build()
        );
        List<HistoryValue> brf4History = List.of(
            HistoryValue.builder().ticker("BRF4").value(300_000_000D).highPrice(80D).lowPrice(70D).openPrice(72D).closePrice(76D).tradeDate(LocalDate.parse("2024-03-18")).build(),
            HistoryValue.builder().ticker("BRF4").value(300_000_000D).highPrice(81D).lowPrice(71D).openPrice(73D).closePrice(77D).tradeDate(LocalDate.parse("2024-03-19")).build(),
            HistoryValue.builder().ticker("BRF4").value(300_000_000D).highPrice(82D).lowPrice(72D).openPrice(74D).closePrice(78D).tradeDate(LocalDate.parse("2024-03-20")).build(),
            HistoryValue.builder().ticker("BRF4").value(300_000_000D).highPrice(83D).lowPrice(73D).openPrice(75D).closePrice(79D).tradeDate(LocalDate.parse("2024-03-21")).build()
        );
        List<HistoryValue> sberHistory = List.of(
            HistoryValue.builder().ticker("SBER").value(300_000_000D).highPrice(280D).lowPrice(270D).openPrice(272D).closePrice(276D).tradeDate(LocalDate.parse("2024-03-18")).build(),
            HistoryValue.builder().ticker("SBER").value(300_000_000D).highPrice(281D).lowPrice(271D).openPrice(273D).closePrice(277D).tradeDate(LocalDate.parse("2024-03-19")).build(),
            HistoryValue.builder().ticker("SBER").value(300_000_000D).highPrice(282D).lowPrice(272D).openPrice(274D).closePrice(278D).tradeDate(LocalDate.parse("2024-03-20")).build(),
            HistoryValue.builder().ticker("SBER").value(300_000_000D).highPrice(283D).lowPrice(273D).openPrice(275D).closePrice(279D).tradeDate(LocalDate.parse("2024-03-21")).build()
        );
        List<HistoryValue> historyValues = new ArrayList<>();
        historyValues.addAll(imoexHistory);
        historyValues.addAll(usdRubHistory);
        historyValues.addAll(brf4History);
        historyValues.addAll(sberHistory);
        return historyValues;
    }

    private List<IntradayValue> getIntradayValues() {
        List<IntradayValue> imoexIntraday = List.of(
            Delta.builder().number(1L).ticker("IMOEX").price(3000D).value(100_000_000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build(),
            Delta.builder().number(2L).ticker("IMOEX").price(3050D).value(200_000_000D).dateTime(LocalDateTime.parse("2024-03-22T11:00:00")).build(),
            Delta.builder().number(3L).ticker("IMOEX").price(3100D).value(300_000_000D).dateTime(LocalDateTime.parse("2024-03-22T12:00:00")).build(),
            Delta.builder().number(4L).ticker("IMOEX").price(3150D).value(400_000_000D).dateTime(LocalDateTime.parse("2024-03-22T12:45:00")).build()
        );
        List<IntradayValue> usdRubIntraday = List.of(
            Deal.builder().number(1L).ticker("USD000UTSTOM").qnt(1000).isBuy(true).price(70D).value(70000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build(),
            Deal.builder().number(2L).ticker("USD000UTSTOM").qnt(1000).isBuy(true).price(72D).value(72000D).dateTime(LocalDateTime.parse("2024-03-22T11:00:00")).build(),
            Deal.builder().number(3L).ticker("USD000UTSTOM").qnt(1000).isBuy(true).price(71D).value(71000D).dateTime(LocalDateTime.parse("2024-03-22T12:00:00")).build(),
            Deal.builder().number(4L).ticker("USD000UTSTOM").qnt(1000).isBuy(false).price(75D).value(75000D).dateTime(LocalDateTime.parse("2024-03-22T12:45:00")).build()
        );
        List<IntradayValue> brf4Intraday = List.of(
            Contract.builder().number(1L).ticker("BRF4").qnt(1).price(120D).value(120000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build(),
            Contract.builder().number(2L).ticker("BRF4").qnt(1).price(111D).value(111000D).dateTime(LocalDateTime.parse("2024-03-22T11:00:00")).build(),
            Contract.builder().number(3L).ticker("BRF4").qnt(1).price(125D).value(125000D).dateTime(LocalDateTime.parse("2024-03-22T12:00:00")).build(),
            Contract.builder().number(4L).ticker("BRF4").qnt(1).price(140D).value(140000D).dateTime(LocalDateTime.parse("2024-03-22T12:45:00")).build()
        );
        List<IntradayValue> sberIntraday = List.of(
            Deal.builder().number(1L).ticker("SBER").qnt(100).isBuy(true).price(270D).value(270000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build(),
            Deal.builder().number(2L).ticker("SBER").qnt(100).isBuy(true).price(272D).value(272000D).dateTime(LocalDateTime.parse("2024-03-22T11:00:00")).build(),
            Deal.builder().number(3L).ticker("SBER").qnt(100).isBuy(true).price(271D).value(271000D).dateTime(LocalDateTime.parse("2024-03-22T12:00:00")).build(),
            Deal.builder().number(4L).ticker("SBER").qnt(100).isBuy(false).price(275D).value(275000D).dateTime(LocalDateTime.parse("2024-03-22T12:45:00")).build()
        );
        List<IntradayValue> intradayValues = new ArrayList<>();
        intradayValues.addAll(imoexIntraday);
        intradayValues.addAll(usdRubIntraday);
        intradayValues.addAll(brf4Intraday);
        intradayValues.addAll(sberIntraday);
        return intradayValues;
    }
}
