package ru.ioque.investfund.scanner.scanning;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.indetity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SCANNER MANAGER TEST - ANOMALY VOLUME ALGORITHM")
public class AnomalyVolumeAlgoTest extends BaseScannerTest {
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
        initTgknAndTgkbAndImoexHistoryTradingData(datasourceId);
        initTgknAndTgkbAndImoexIntradayData(datasourceId);
        initScanner(datasourceId, "TGKN", "TGKB", "IMOEX");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 2, 2,2, 0);
        assertFinInstrument(getTgkn(), 100.0, 102.0, 13000.0, 1150.0, 100.0, 99.0, true);
        assertFinInstrument(getTgkb(), 100.0, 102.0, 15000.0, 1500.0, 100.0, 99.0, true);
        assertFinInstrument(getImoex(), 2800.0, 3100.0, 2_200_000.0, 1_500_000.0, 3000.0, 2900.0, true);
    }

    @Test
    @DisplayName("""
        T2. С последнего запуска прошло меньше минуты, сканер не запущен.
        """)
    void testCase2() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTgknAndTgkbAndImoexHistoryTradingData(datasourceId);
        initTgknAndTgkbAndImoexIntradayData(datasourceId);
        initScanner(datasourceId, "TGKN", "TGKB", "IMOEX");
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-22T13:00:30");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 2, 2, 2, 0);
        assertFinInstrument(getTgkn(), 100.0, 102.0, 13000.0, 1150.0, 100.0, 99.0, true);
        assertFinInstrument(getTgkb(), 100.0, 102.0, 15000.0, 1500.0, 100.0, 99.0, true);
        assertFinInstrument(getImoex(), 2800.0, 3100.0, 2_200_000.0, 1_500_000.0, 3000.0, 2900.0, true);
    }

    @Test
    @DisplayName("""
        T3. С последнего запуска прошла минута, сканер запущен.
        """)
    void testCase3() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTgknAndTgkbAndImoexHistoryTradingData(datasourceId);
        initTgknAndTgkbAndImoexIntradayData(datasourceId);
        initScanner(datasourceId, "TGKN", "TGKB", "IMOEX");
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-22T13:01:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 2, 2, 2, 0);
        assertFinInstrument(getTgkn(), 100.0, 102.0, 13000.0, 1150.0, 100.0, 99.0, true);
        assertFinInstrument(getTgkb(), 100.0, 102.0, 15000.0, 1500.0, 100.0, 99.0, true);
        assertFinInstrument(getImoex(), 2800.0, 3100.0, 2_200_000.0, 1_500_000.0, 3000.0, 2900.0, true);
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
        initTgknBuySignalDataset(datasourceId);
        initScanner(datasourceId, "TGKN", "IMOEX");
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-24T12:00:00");
        initTgknSellSignalDataset(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 2, 1, 1, 1);
        assertFinInstrument(getTgkn(), 98.0, 96.0, 13000.0, 1450.0, 97.1, 99.1, false);
        assertFinInstrument(getImoex(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
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
        initTradingResults(
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-21", 100.D, 100.D, 100D, 1000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-10", 99.D, 3000.0, 1_500_000.0),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-20", 99.D, 2900.0, 1_500_000.0),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-21", 100.D, 3000.0, 1_500_000.0)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L,"10:00:00", 3000.0, 1_000_000D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L,  "12:00:00", 2900.0, 2_000_000D)
        );
        initScanner(datasourceId, "TGKN", "IMOEX");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertFinInstrument(getTgkn(), null, null, null, 1000.0, 100.0, 99.0, null);
        assertFinInstrument(getImoex(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
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
        initTradingResults(
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-10", 3000.0, 3000.0, 1_500_000.0),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-20", 2900.0, 2900.0, 1_500_000.0),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-21", 3000.0, 3000.0, 1_500_000.0)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L,  "10:00:00", 3000.0, 1_000_000D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L, "12:00:00", 2900.0, 2_000_000D)
        );
        initScanner(datasourceId, "TGKN", "IMOEX");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertFinInstrument(getTgkn(), null, null, null, null, null, null, null);
        assertFinInstrument(getImoex(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
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
        initTradingResults(
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-21", 100.D, 100.D, 100D, 1000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-10", 2900.0, 2900.0, 1_500_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId), "2023-12-20", 2900.0, 2900.0, 1_500_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId), "2023-12-21", 3000.0, 3000.0, 1_500_000D)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L,  "10:00:00", 3000.0, 1_000_000D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L,  "12:00:00", 2900.0, 2_000_000D),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),1L,  "10:00:00", 100D, 469D, 1)
        );
        initScanner(datasourceId, "TGKN", "IMOEX");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertFinInstrument(getTgkn(), 100D, 100D, 469D, 1000D, 100.D, 99.D, false);
        assertFinInstrument(getImoex(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
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
        initTradingResults(
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-10", 2900.D, 2900.D, 1_500_000.0),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId), "2023-12-20", 2900.D, 2900.D, 1_500_000.0),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-21", 3000.D, 3000.D, 1_500_000.0)
        );
        initDealDatas(
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),1L, "10:00:00", 100D, 6000D, 1),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),2L, "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId),3L, "11:00:00", 100D, 6000D, 1)
        );
        initScanner(datasourceId, "TGKN", "IMOEX");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertFinInstrument(getTgkn(), 100D, 100D, 13000D, null, null, null, null);
        assertFinInstrument(getImoex(), null, null, null, 1_500_000.0, 3000.0, 2900.0, null);
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
        initTradingResults(
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L,  "10:00:00", 2900D, 1_000_000D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L,  "12:00:00", 3000D, 2_000_000D),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),1L, "10:00:00", 100D, 6000D, 1),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),2L, "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId),3L, "11:00:00", 103D, 6000D, 1)
        );
        initScanner(datasourceId, "TGKN", "IMOEX");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertFinInstrument(getTgkn(), 100D, 103D, 13000D, 1000D, 100.D, 99.D, true);
        assertFinInstrument(getImoex(), 2900D, 3000D, 3_000_000D, null, null, null, null);
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
        initTradingResults(
            buildDealResultBy(new InstrumentId("TGKN", datasourceId), "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId), "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L,  "10:00:00", 2900D, 1_000_000D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L,  "12:00:00", 3000D, 2_000_000D)
        );
        initScanner(datasourceId, "TGKN", "IMOEX");

        runWorkPipeline(datasourceId);

        assertEquals(0, getSignals().size());
        assertSignals(getSignals(), 0, 0,0, 0);
        assertFinInstrument(getTgkn(), null, null, null, 1000D, 100.D, 99.D, null);
        assertFinInstrument(getImoex(), 2900D, 3000D, 3_000_000D, null, null, null, null);
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
        initTradingResults(
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId), "2023-12-10", 2800D, 2800D, 1_000_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-20", 2800D, 2800D, 1_000_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-21", 2800D, 2800D, 1_000_000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId), "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L, "10:00:00", 2900D, 1_000_000D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L, "12:00:00", 3000D, 2_000_000D),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),1L, "10:00:00", 100D, 6000D, 1),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),2L, "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId),3L,  "11:00:00", 103D, 6000D, 1)
        );
        initScanner(datasourceId, "TGKN", "IMOEX");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0, 0);
        assertFinInstrument(getTgkn(), 100D, 103D, 13000D, null, 100.D, null, true);
        assertFinInstrument(getImoex(), 2900D, 3000D, 3_000_000D, 1_000_000D, 2800D, 2800D, true);
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
        initTradingResults(
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-21", 2800D, 2800D, 1_000_000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L, "10:00:00", 2800D, 1_000_000D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L, "12:00:00", 3000D, 2_000_000D),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),1L, "10:00:00", 100D, 6000D, 1),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),2L, "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId),3L, "11:00:00", 100D, 6000D, 1)
        );
        initScanner(datasourceId, "TGKN", "IMOEX");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0, 0);
        assertFinInstrument(getTgkn(), 100D, 100D, 13000D, null, 100.D, null, false);
        assertFinInstrument(getImoex(), 2800D, 3000D, 3_000_000D, null, 2800D, null, true);
    }

    public void assertFinInstrument(
        TradingSnapshot tradingSnapshot,
        Double openPrice,
        Double lastPrice,
        Double todayValue,
        Double historyMedianValue,
        Double prevClosePrice,
        Double prevPrevClosePrice,
        Boolean isRiseToday
    ) {
        assertEquals(openPrice, tradingSnapshot.getTodayOpenPrice().orElse(null));
        assertEquals(lastPrice, tradingSnapshot.getTodayLastPrice().orElse(null));
        assertEquals(todayValue, tradingSnapshot.getTodayValue().orElse(null));
        assertEquals(historyMedianValue, tradingSnapshot.getHistoryMedianValue(HISTORY_PERIOD).orElse(null));
        assertEquals(prevClosePrice, tradingSnapshot.getPrevClosePrice().orElse(null));
        assertEquals(prevPrevClosePrice, tradingSnapshot.getPrevPrevClosePrice().orElse(null));
        assertEquals(isRiseToday, tradingSnapshot.isRiseToday().orElse(null));
    }

    private void initTgknBuySignalDataset(DatasourceId datasourceId) {
        initTradingResults(
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-20", 99.D, 99.D, 99D, 2000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-21", 100.D, 100.D, 100D, 1400D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-10", 2900D, 2900D, 1_000_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-20", 2900D, 2900D, 1_500_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-21", 3000D, 3000D, 2_000_000D)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L,"10:00:00", 2800D, 100D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L,"12:00:00", 3200D, 200D),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),1L,"10:00:00", 100D, 5000D, 1),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),2L,"10:03:00", 100D, 1000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId),3L,"11:00:00", 100D, 1000D, 1),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),4L,"11:01:00", 100D, 1000D, 1),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId),5L, "11:45:00", 102D, 5000D, 1)
        );
    }

    private void initTgknSellSignalDataset(DatasourceId datasourceId) {
        initTradingResults(
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-22", 99.D, 99.1D, 97D, 2000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-23", 99.D, 99.1D, 97D, 1000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-24", 97.2D, 97.1D, 97D, 1500D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-22", 2900D, 2900D, 1_000_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-23", 2900D, 2900D, 1_500_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initDealDatas(
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId), 3L,"10:00:00", 3000D, 1_000_000D),
            buildDeltaBy(new InstrumentId("IMOEX", datasourceId), 4L,"12:00:00", 2900D, 2_000_000D),
            buildBuyDealBy(new InstrumentId("TGKN", datasourceId), 6L,"10:00:00", 98D, 5000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId), 7L,"10:03:00", 97D, 1000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId), 8L,"11:00:00", 98D, 1000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId), 9L,"11:01:00", 97D, 1000D, 1),
            buildSellDealBy(new InstrumentId("TGKN", datasourceId), 10L,"11:45:00", 96D, 5000D, 1)
        );
    }

    private void initTgknAndTgkbAndImoexHistoryTradingData(DatasourceId datasourceId) {
        initTradingResults(
            buildDealResultBy(new InstrumentId("TGKB", datasourceId),"2023-12-19", 99.D, 99.D, 1D, 2000D),
            buildDealResultBy(new InstrumentId("TGKB", datasourceId),"2023-12-20", 99.D, 99.D, 1D, 1000D),
            buildDealResultBy(new InstrumentId("TGKB", datasourceId),"2023-12-21", 100.D, 100.D, 1D, 1500D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-19", 99.D, 99.D, 1D, 3000D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-20", 99.D, 99.D, 1D, 1150D),
            buildDealResultBy(new InstrumentId("TGKN", datasourceId),"2023-12-21", 100.D, 100.D, 1D, 1100D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-10", 2800D, 2900D, 1_000_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-20", 2800D, 2900D, 1_500_000D),
            buildDeltaResultBy(new InstrumentId("IMOEX", datasourceId),"2023-12-21", 2900D, 3000D, 2_000_000D)
        );
    }

    private void initTgknAndTgkbAndImoexIntradayData(DatasourceId datasourceId) {
        datasourceStorage().initDealDatas(
            List.of(
                buildDeltaBy(new InstrumentId("IMOEX", datasourceId),1L, "10:00:00", 2800D, 1_000_000D),
                buildDeltaBy(new InstrumentId("IMOEX", datasourceId),2L,"12:00:00", 3100D, 1_200_000D),
                //TGKB
                buildBuyDealBy(new InstrumentId("TGKB", datasourceId),1L,"10:00:00", 100D, 6000D, 1),
                buildBuyDealBy(new InstrumentId("TGKB", datasourceId),2L,"10:16:00", 100D, 1000D, 1),
                buildBuyDealBy(new InstrumentId("TGKB", datasourceId),3L,"11:00:00", 100D, 1000D, 1),
                buildBuyDealBy(new InstrumentId("TGKB", datasourceId),4L,"11:10:00", 100D, 1000D, 1),
                buildBuyDealBy(new InstrumentId("TGKB", datasourceId),5L, "11:50:00", 102D, 6000D, 1),
                //TGKN
                buildBuyDealBy(new InstrumentId("TGKN", datasourceId),1L,"10:00:00", 100D, 5000D, 1),
                buildBuyDealBy(new InstrumentId("TGKN", datasourceId),2L,"10:03:00", 100D, 1000D, 1),
                buildBuyDealBy(new InstrumentId("TGKN", datasourceId),3L,"11:00:00", 100D, 1000D, 1),
                buildBuyDealBy(new InstrumentId("TGKN", datasourceId),4L,"11:01:00", 100D, 1000D, 1),
                buildBuyDealBy(new InstrumentId("TGKN", datasourceId),5L,"11:45:00", 102D, 5000D, 1)
            )
        );
    }

    private void initTgknAndTgkbAndImoex(DatasourceId datasourceId) {
        datasourceStorage().initInstruments(
            List.of(
                imoex(),
                tgkb(),
                tgkn()
            )
        );
        commandBus().execute(new IntegrateInstrumentsCommand(datasourceId));
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, getTickers(datasourceId)));
    }

    private void initScanner(DatasourceId datasourceId, String... tickers) {


        commandBus().execute(
            CreateScannerCommand.builder()
                .workPeriodInMinutes(1)
                .description("Аномальные объемы, третий эшелон.")
                .instrumentIds(Arrays.stream(tickers).map(ticker -> new InstrumentId(ticker, datasourceId)).toList())
                .properties(
                    AnomalyVolumeProperties.builder()
                        .indexTicker("IMOEX")
                        .historyPeriod(HISTORY_PERIOD)
                        .scaleCoefficient(1.5)
                        .build()
                )
                .build()
        );
    }

    private static final int HISTORY_PERIOD = 2;
}
