package ru.ioque.investfund.scanner.algorithm;

import java.util.Arrays;
import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.IMOEX;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.TGKB;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.TGKN;

@DisplayName("ANOMALY VOLUME ALGORITHM TEST")
public class AnomalyVolumeAlgorithmTest extends BaseAlgorithmTest {
    @Test
    @SneakyThrows
    @DisplayName("""
        Т1. Создан сканер сигналов AnomalyVolumeScannerSignal для двух инструментов.
        По обоим инструментам есть торговые данные. По обоим инструментам объемы превышают средневзвешенные больше, чем
        в scaleCoefficient-раз.
        Результат: зарегистрировано два сигнала.
        """)
    void testCase1() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTgknAndTgkbAndImoexHistoryTradingData();
        initTgknAndTgkbAndImoexIntradayData();
        initDefaultScanner(datasourceId);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 2,2, 0);
        assertFinInstrument(getTgknPerformance(), 100.0, 102.0, 13000.0, 1150.0, 100.0, 99.0, true);
        assertFinInstrument(getTgkbPerformance(), 100.0, 102.0, 15000.0, 1500.0, 100.0, 99.0, true);
        assertFinInstrument(getImoexPerformance(), 2800.0, 3100.0, 2_200_000.0, 1_500_000.0, 3000.0, 2900.0, true);
    }

    private void initDefaultScanner(DatasourceId datasourceId) {
        initScanner(datasourceId, TGKN, TGKB, IMOEX);
    }

    @Test
    @DisplayName("""
        T4. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Ранее был зарегистрирован сигнал к покупке. В текущем массиве данных
        объем торгов провышает медиану в scaleCoefficient-раз. Объем продаж превышает объем покупок.
        Результат: зарегистрирован сигнал к продаже. Сигнал к покупке закрыт.
        """)
    void testCase4() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTgknBuySignalDataset();
        initTgknAndImoexScanner(datasourceId);
        runPipeline(datasourceId);

        initTodayDateTime("2023-12-25T12:00:00");
        initTgknSellSignalDataset();
        runPipeline(datasourceId);

        assertSignals(getSignals(), 2, 1, 1);
        assertFinInstrument(getTgknPerformance(), 98.0, 96.0, 13000.0, 1450.0, 97.1, 99.1, false);
        assertFinInstrument(getImoexPerformance(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    private void initTgknAndImoexScanner(DatasourceId datasourceId) {
        initScanner(datasourceId, TGKN, IMOEX);
    }

    @Test
    @DisplayName("""
        T5. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутри дня сделок по бумаге не было.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase5() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initAggregatedTotals(
            historyFixture.tgknHistoryValue("2023-12-19", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-20", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 100D, 1000D),
            historyFixture.imoexHistoryValue("2023-12-10", 99.D, 3000.0, 1_500_000.0),
            historyFixture.imoexHistoryValue("2023-12-20", 99.D, 2900.0, 1_500_000.0),
            historyFixture.imoexHistoryValue("2023-12-21", 100.D, 3000.0, 1_500_000.0)
        );
        initIntradayData(
            intradayFixture.imoexDelta(1L,"10:00:00", 3000.0, 1_000_000D),
            intradayFixture.imoexDelta(2L, "12:00:00", 2900.0, 2_000_000D)
        );
        initTgknAndImoexScanner(datasourceId);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0,0, 0);
        assertFinInstrument(getTgknPerformance(), 0.0, 0.0, 0.0, 1000.0, 100.0, 99.0, false);
        assertFinInstrument(getImoexPerformance(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    @Test
    @DisplayName("""
        T6. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные не проинтегрированы, внутри дня сделок по бумаге не было.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase6() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initAggregatedTotals(
            historyFixture.imoexHistoryValue("2023-12-10", 3000.0, 3000.0, 1_500_000.0),
            historyFixture.imoexHistoryValue("2023-12-20", 2900.0, 2900.0, 1_500_000.0),
            historyFixture.imoexHistoryValue("2023-12-21", 3000.0, 3000.0, 1_500_000.0)
        );
        initIntradayData(
            intradayFixture.imoexDelta(1L,"10:00:00", 3000.0, 1_000_000D),
            intradayFixture.imoexDelta(2L,"12:00:00", 2900.0, 2_000_000D)
        );
        initTgknAndImoexScanner(datasourceId);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0,0, 0);
        assertFinInstrument(getTgknPerformance(), 0.0, 0.0, 0.0, null, null, null, null);
        assertFinInstrument(getImoexPerformance(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    @Test
    @DisplayName("""
        T7. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутри дня была совершена одна сделка.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase7() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initAggregatedTotals(
            historyFixture.tgknHistoryValue("2023-12-19", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-20", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 100D, 1000D),
            historyFixture.imoexHistoryValue("2023-12-10", 2900.0, 2900.0, 1_500_000D),
            historyFixture.imoexHistoryValue("2023-12-20", 2900.0, 2900.0, 1_500_000D),
            historyFixture.imoexHistoryValue("2023-12-21", 3000.0, 3000.0, 1_500_000D)
        );
        initIntradayData(
            intradayFixture.imoexDelta(1L, "10:00:00", 3000.0, 1_000_000D),
            intradayFixture.imoexDelta(2L,"12:00:00", 2900.0, 2_000_000D),
            intradayFixture.tgknBuyDeal(1L,"10:00:00", 100D, 469D, 1)
        );
        initTgknAndImoexScanner(datasourceId);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0,0, 0);
        assertFinInstrument(getTgknPerformance(), 100D, 100D, 469D, 1000D, 100.D, 99.D, false);
        assertFinInstrument(getImoexPerformance(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    @Test
    @DisplayName("""
        T8. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        Исторические данные по индексу есть, внутридневных данных по индексу нет.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase8() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initAggregatedTotals(
            historyFixture.imoexHistoryValue("2023-12-10", 2900.D, 2900.D, 1_500_000.0),
            historyFixture.imoexHistoryValue("2023-12-20", 2900.D, 2900.D, 1_500_000.0),
            historyFixture.imoexHistoryValue("2023-12-21", 3000.D, 3000.D, 1_500_000.0)
        );
        initIntradayData(
            intradayFixture.tgknBuyDeal(1L,"10:00:00", 100D, 6000D, 1),
            intradayFixture.tgknBuyDeal(2L,"10:03:00", 100D, 1000D, 1),
            intradayFixture.tgknSellDeal(3L,"11:00:00", 100D, 6000D, 1)
        );
        initTgknAndImoexScanner(datasourceId);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0,0, 0);
        assertFinInstrument(getTgknPerformance(), 100D, 100D, 13000D, null, null, null, null);
        assertFinInstrument(getImoexPerformance(), 0.0, 0.0, 0.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    @Test
    @DisplayName("""
        T9. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Исторических данных по индексу нет, внутридневные данные по индексу есть.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase9() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initAggregatedTotals(
            historyFixture.tgknHistoryValue("2023-12-19", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-20", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initIntradayData(
            intradayFixture.imoexDelta(1L, "10:00:00", 2900D, 1_000_000D),
            intradayFixture.imoexDelta(2L, "12:00:00", 3000D, 2_000_000D),
            intradayFixture.tgknBuyDeal(1L, "10:00:00", 100D, 6000D, 1),
            intradayFixture.tgknBuyDeal(2L,"10:03:00", 100D, 1000D, 1),
            intradayFixture.tgknSellDeal(3L, "11:00:00", 103D, 6000D, 1)
        );
        initTgknAndImoexScanner(datasourceId);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0,0, 0);
        assertFinInstrument(getTgknPerformance(), 100D, 103D, 13000D, 1000D, 100.D, 99.D, true);
        assertFinInstrument(getImoexPerformance(), 2900D, 3000D, 3_000_000D, null, null, null, null);
    }

    @Test
    @DisplayName("""
        T10. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Исторических данных по индексу нет, внутридневные данные по индексу есть.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase18() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initAggregatedTotals(
            historyFixture.tgknHistoryValue("2023-12-19", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-20", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initIntradayData(
            intradayFixture.imoexDelta(1L, "10:00:00", 2900D, 1_000_000D),
            intradayFixture.imoexDelta(2L, "12:00:00", 3000D, 2_000_000D)
        );
        initTgknAndImoexScanner(datasourceId);

        runPipeline(datasourceId);

        assertEquals(0, getSignals().size());
        assertSignals(getSignals(),0,0, 0);
        assertFinInstrument(getTgknPerformance(), 0.0, 0.0, 0.0, 1000D, 100.D, 99.D, false);
        assertFinInstrument(getImoexPerformance(), 2900D, 3000D, 3_000_000D, null, null, null, null);
    }

    @Test
    @DisplayName("""
        T11. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы за один день, внутридневные данные проинтегрированы, объем превышает объем за предыдущий день.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигнала нет, недостаточно данных для расчета медианы (меньше historyPeriod).
        """)
    void testCase19() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initAggregatedTotals(
            historyFixture.imoexHistoryValue("2023-12-10", 2800D, 2800D, 1_000_000D),
            historyFixture.imoexHistoryValue("2023-12-20", 2800D, 2800D, 1_000_000D),
            historyFixture.imoexHistoryValue("2023-12-21", 2800D, 2800D, 1_000_000D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initIntradayData(
            intradayFixture.imoexDelta(1L, "10:00:00", 2900D, 1_000_000D),
            intradayFixture.imoexDelta(2L, "12:00:00", 3000D, 2_000_000D),
            intradayFixture.tgknBuyDeal(1L,"10:00:00", 100D, 6000D, 1),
            intradayFixture.tgknBuyDeal(2L, "10:03:00", 100D, 1000D, 1),
            intradayFixture.tgknSellDeal(3L, "11:00:00", 103D, 6000D, 1)
        );
        initTgknAndImoexScanner(datasourceId);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
        assertFinInstrument(getTgknPerformance(), 100D, 103D, 13000D, null, 100.D, null, true);
        assertFinInstrument(getImoexPerformance(), 2900D, 3000D, 3_000_000D, 1_000_000D, 2800D, 2800D, true);
    }

    @Test
    @DisplayName("""
        T12. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные проинтегрированы, объем превышает исторический.
        Исторические данные по индексу проинтегрированы за один день, дневные данные по индексу проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase20() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initAggregatedTotals(
            historyFixture.imoexHistoryValue("2023-12-21", 2800D, 2800D, 1_000_000D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initIntradayData(
            intradayFixture.imoexDelta(1L, "10:00:00", 2800D, 1_000_000D),
            intradayFixture.imoexDelta(2L, "12:00:00", 3000D, 2_000_000D),
            intradayFixture.tgknBuyDeal(1L,"10:00:00", 100D, 6000D, 1),
            intradayFixture.tgknBuyDeal(2L,"10:03:00", 100D, 1000D, 1),
            intradayFixture.tgknSellDeal(3L, "11:00:00", 100D, 6000D, 1)
        );
        initTgknAndImoexScanner(datasourceId);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertFinInstrument(getTgknPerformance(), 100D, 100D, 13000D, null, 100.D, null, false);
        assertFinInstrument(getImoexPerformance(), 2800D, 3000D, 3_000_000D, null, 2800D, null, true);
    }

    public void assertFinInstrument(
        InstrumentPerformance instrumentPerformance,
        Double openPrice,
        Double lastPrice,
        Double todayValue,
        Double historyMedianValue,
        Double prevClosePrice,
        Double prevPrevClosePrice,
        Boolean isRiseToday
    ) {
        assertEquals(openPrice, instrumentPerformance.getIntradayPerformance().map(IntradayPerformance::getTodayFirstPrice).orElse(null));
        assertEquals(lastPrice, instrumentPerformance.getIntradayPerformance().map(IntradayPerformance::getTodayLastPrice).orElse(null));
        assertEquals(todayValue, instrumentPerformance.getIntradayPerformance().map(IntradayPerformance::getTodayValue).orElse(null));
        assertEquals(historyMedianValue, instrumentPerformance.getHistoryMedianValue(HISTORY_PERIOD).orElse(null));
        assertEquals(prevClosePrice, instrumentPerformance.getPrevClosePrice().orElse(null));
        assertEquals(prevPrevClosePrice, instrumentPerformance.getPrevPrevClosePrice().orElse(null));
        assertEquals(isRiseToday, instrumentPerformance.isRiseToday().orElse(null));
    }

    private void initTgknBuySignalDataset() {
        initAggregatedTotals(
            historyFixture.tgknHistoryValue("2023-12-19", 99.D, 99.D, 99D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-20", 99.D, 99.D, 99D, 2000D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 100D, 1400D),
            historyFixture.imoexHistoryValue("2023-12-10", 2900D, 2900D, 1_000_000D),
            historyFixture.imoexHistoryValue("2023-12-20", 2900D, 2900D, 1_500_000D),
            historyFixture.imoexHistoryValue("2023-12-21", 3000D, 3000D, 2_000_000D)
        );
        initIntradayData(
            intradayFixture.imoexDelta( 1L, "10:00:00", 2800D, 100D),
            intradayFixture.imoexDelta( 2L, "12:00:00", 3200D, 200D),
            intradayFixture.tgknBuyDeal( 1L, "10:00:00", 100D, 5000D, 1),
            intradayFixture.tgknBuyDeal( 2L, "10:03:00", 100D, 1000D, 1),
            intradayFixture.tgknSellDeal(3L, "11:00:00", 100D, 1000D, 1),
            intradayFixture.tgknBuyDeal( 4L, "11:01:00", 100D, 1000D, 1),
            intradayFixture.tgknBuyDeal( 5L, "11:45:00", 102D, 5000D, 1)
        );
    }

    private void initTgknSellSignalDataset() {
        initAggregatedTotals(
            historyFixture.tgknHistoryValue("2023-12-22", 99.D, 99.1D, 97D, 2000D),
            historyFixture.tgknHistoryValue("2023-12-23", 99.D, 99.1D, 97D, 1000D),
            historyFixture.tgknHistoryValue("2023-12-24", 97.2D, 97.1D, 97D, 1500D),
            historyFixture.imoexHistoryValue("2023-12-22", 2900D, 2900D, 1_000_000D),
            historyFixture.imoexHistoryValue("2023-12-23", 2900D, 2900D, 1_500_000D),
            historyFixture.imoexHistoryValue("2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initIntradayData(
            intradayFixture.imoexDelta( 3L,"10:00:00", 3000D, 1_000_000D),
            intradayFixture.imoexDelta( 4L,"12:00:00", 2900D, 2_000_000D),
            intradayFixture.tgknBuyDeal( 6L,"10:00:00", 98D, 5000D, 1),
            intradayFixture.tgknSellDeal(7L,"10:03:00", 97D, 1000D, 1),
            intradayFixture.tgknSellDeal(8L,"11:00:00", 98D, 1000D, 1),
            intradayFixture.tgknSellDeal(9L,"11:01:00", 97D, 1000D, 1),
            intradayFixture.tgknSellDeal(10L,"11:45:00", 96D, 5000D, 1)
        );
    }

    private void initTgknAndTgkbAndImoexHistoryTradingData() {
        initAggregatedTotals(
            historyFixture.tgkbHistoryValue("2023-12-19", 99.D, 99.D, 1D, 2000D),
            historyFixture.tgkbHistoryValue("2023-12-20", 99.D, 99.D, 1D, 1000D),
            historyFixture.tgkbHistoryValue("2023-12-21", 100.D, 100.D, 1D, 1500D),
            historyFixture.tgknHistoryValue("2023-12-19", 99.D, 99.D, 1D, 3000D),
            historyFixture.tgknHistoryValue("2023-12-20", 99.D, 99.D, 1D, 1150D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 1D, 1100D),
            historyFixture.imoexHistoryValue("2023-12-10", 2800D, 2900D, 1_000_000D),
            historyFixture.imoexHistoryValue("2023-12-20", 2800D, 2900D, 1_500_000D),
            historyFixture.imoexHistoryValue("2023-12-21", 2900D, 3000D, 2_000_000D)
        );
    }

    private void initTgknAndTgkbAndImoexIntradayData() {
        datasourceStorage().initDealDatas(
            List.of(
                intradayFixture.imoexDelta( 1L, "10:00:00", 2800D, 1_000_000D),
                intradayFixture.imoexDelta( 2L, "12:00:00", 3100D, 1_200_000D),
                //TGKB
                intradayFixture.tgkbBuyDeal( 1L,"10:00:00", 100D, 6000D, 1),
                intradayFixture.tgkbBuyDeal( 2L,"10:16:00", 100D, 1000D, 1),
                intradayFixture.tgkbBuyDeal( 3L,"11:00:00", 100D, 1000D, 1),
                intradayFixture.tgkbBuyDeal( 4L,"11:10:00", 100D, 1000D, 1),
                intradayFixture.tgkbBuyDeal( 5L,"11:50:00", 102D, 6000D, 1),
                //TGKN
                intradayFixture.tgknBuyDeal( 1L,"10:00:00", 100D, 5000D, 1),
                intradayFixture.tgknBuyDeal( 2L,"10:03:00", 100D, 1000D, 1),
                intradayFixture.tgknBuyDeal( 3L,"11:00:00", 100D, 1000D, 1),
                intradayFixture.tgknBuyDeal( 4L,"11:01:00", 100D, 1000D, 1),
                intradayFixture.tgknBuyDeal( 5L,"11:45:00", 102D, 5000D, 1)
            )
        );
    }

    private void initTgknAndTgkbAndImoex(DatasourceId datasourceId) {
        datasourceStorage().initInstrumentDetails(
            List.of(
                instrumentFixture.imoexDetails(),
                instrumentFixture.tgkbDetails(),
                instrumentFixture.tgknDetails()
            )
        );
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(new EnableUpdateInstruments(datasourceId, getTickers(datasourceId)));
    }

    private void initScanner(DatasourceId datasourceId, String... tickers) {
        commandBus().execute(
            CreateScanner.builder()
                .workPeriodInMinutes(1)
                .description("Аномальные объемы, третий эшелон.")
                .datasourceId(datasourceId)
                .tickers(Arrays.stream(tickers).map(Ticker::from).toList())
                .properties(
                    AnomalyVolumeProperties.builder()
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(HISTORY_PERIOD)
                        .scaleCoefficient(1.5)
                        .build()
                )
                .build()
        );
    }

    private static final int HISTORY_PERIOD = 2;
}
