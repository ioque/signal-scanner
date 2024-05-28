package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANNER MANAGER TEST - CORRELATION SECTORAL ALGORITHM")
public class CorrelationSectoralAlgoTest extends BaseScannerTest {
    private static final double futuresOvernightScale = 0.02;
    private static final double stockOvernightScale = 0.005;
    private static final String startDate = "2023-12-22T13:00:00";

    @Test
    @DisplayName("""
        T1. TATN росла вчера, BRF4 рос вчера.
        Сигнал зафиксирован.
        """)
    void testCase1() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals();
        initPositiveDealResults();
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(),1, 1, 0);
        assertTrue(getTatnPerformance().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4Performance().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T2. TATN росла вчера, BRF4 падал вчера.
        Сигнал не зафиксирован.
        """)
    void testCase2() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initNegativeDeals();
        initNegativeDealResults();
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getTatnPerformance().isRiseOvernight(stockOvernightScale));
        assertFalse(getBrf4Performance().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T3. С последнего запуска прошло меньше 24 часов, сканер не запущен.
        """)
    void testCase3() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals();
        initPositiveDealResults();
        initScanner(datasourceId);
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-22T18:00:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getTatnPerformance().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4Performance().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T4. С последнего запуска прошло 24 часа, сканер запущен.
        """)
    void testCase4() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals();
        initPositiveDealResults();
        initScanner(datasourceId);
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-23T13:00:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getTatnPerformance().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4Performance().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T5. Исторические данные проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы за один день, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигнал зафиксирован.
        """)
    void testCase5() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals();
        initHistoryValues(
            buildBrf4HistoryValue("2023-12-21", 80D, 80D, 10D),
            buildTatnHistoryValue("2023-12-20", 251D, 252D, 1D, 1D),
            buildTatnHistoryValue("2023-12-21", 252D, 253D, 1D, 1D)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertEquals(1, scannerRepository().findAllBy(getDatasourceId()).get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T6. Исторические данные проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные не проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase6() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDealResults();
        initIntradayValues(
            buildTatnBuyDeal(1L, "10:00:00", 251.1D, 136926D, 1),
            buildTatnBuyDeal(2L, "12:00:00", 247.1D, 136926D, 1),
            buildTatnBuyDeal(3L,"13:45:00", 280.1D, 136926D, 1)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getTatnPerformance().isRiseOvernight(stockOvernightScale));
        assertFalse(getBrf4Performance().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T7. Исторические данные не проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase7() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals();
        initHistoryValues(
            buildBrf4HistoryValue("2023-12-20", 75D, 75D, 10D),
            buildBrf4HistoryValue("2023-12-21", 80D, 80D, 10D)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertFalse(getTatnPerformance().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4Performance().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T8. Исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase8() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDealResults();
        initIntradayValues(
            buildBrf4Contract(1L, "10:00:00", 78D, 78000D, 1),
            buildBrf4Contract(2L, "12:00:00", 96D, 96000D, 1)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertFalse(getTatnPerformance().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4Performance().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T9. Исторические данные проинтегрированы за один день, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase9() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals();
        initHistoryValues(
            buildBrf4HistoryValue("2023-12-20", 75D, 75D, 10D),
            buildBrf4HistoryValue("2023-12-21", 80D, 80D, 10D),
            buildTatnHistoryValue("2023-12-21", 252D, 253D, 1D, 1D)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(),1, 1, 0);
        assertTrue(getTatnPerformance().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4Performance().isRiseOvernight(futuresOvernightScale));
    }

    private void initScanner(DatasourceId datasourceId) {
        commandBus().execute(
            CreateScanner.builder()
                .workPeriodInMinutes(1)
                .description("Корреляция сектора с фьючерсом.")
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .properties(
                    SectoralFuturesProperties.builder()
                        .futuresOvernightScale(futuresOvernightScale)
                        .stockOvernightScale(stockOvernightScale)
                        .futuresTicker(new Ticker(BRF4))
                        .build()
                )
                .build()
        );
    }

    private void initPositiveDealResults() {
        datasourceStorage().initHistoryValues(
            List.of(
                buildBrf4HistoryValue("2023-12-20", 75D, 75D, 10D),
                buildBrf4HistoryValue("2023-12-21", 80D, 80D, 10D),
                buildTatnHistoryValue("2023-12-20", 251D, 252D, 1D, 1D),
                buildTatnHistoryValue("2023-12-21", 252D, 253D, 1D, 1D)
            )
        );
    }

    private void initNegativeDealResults() {
        initHistoryValues(
            buildBrf4HistoryValue("2023-12-20", 75D, 75D, 10D),
            buildBrf4HistoryValue("2023-12-21", 80D, 74D, 10D),
            buildTatnHistoryValue("2023-12-20", 251D, 252D, 1D, 1D),
            buildTatnHistoryValue("2023-12-21", 252D, 253D, 1D, 1D)
        );
    }

    private void initNegativeDeals() {
        initIntradayValues(
            buildBrf4Contract(1L,"10:00:00", 73D, 73000D, 1),
            buildBrf4Contract(2L,"12:00:00", 72D, 73000D, 1),
            buildTatnBuyDeal(1L,"10:00:00", 251.1D, 136926D, 1),
            buildTatnBuyDeal(2L,"12:00:00", 247.1D, 136926D, 1),
            buildTatnBuyDeal(3L,"13:45:00", 280.1D, 136926D, 1)
        );
    }

    private void initPositiveDeals() {
        initIntradayValues(
            buildBrf4Contract(1L,"10:00:00", 78D, 78000D, 1),
            buildBrf4Contract(2L,"12:00:00", 96D, 96000D, 1),
            buildTatnBuyDeal(1L,"10:00:00", 251.1D, 136926D, 1),
            buildTatnBuyDeal(2L,"12:00:00", 247.1D, 136926D, 1),
            buildTatnBuyDeal(3L, "13:45:00", 280.1D, 136926D, 1)
        );
    }

    private void initInstruments(DatasourceId datasourceId) {
        datasourceStorage()
            .initInstrumentDetails(
                List.of(
                    tatnDetails(),
                    brf4()
                )
            );
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(new EnableUpdateInstruments(datasourceId, getTickers(datasourceId)));
    }
}
