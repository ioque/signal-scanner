package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.SectoralFuturesProperties;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANNER MANAGER TEST - CORRELATION SECTORAL ALGORITHM")
public class CorrelationSectoralAlgoTest extends BaseScannerTest {
    private static final double futuresOvernightScale = 0.02;
    private static final double stockOvernightScale = 0.005;
    private static final List<String> tickers = List.of(TATN, BRF4);
    private static final String startDate = "2023-12-22T13:00:00";

    @Test
    @DisplayName("""
        T1. TATN росла вчера, BRF4 рос вчера.
        Сигнал зафиксирован.
        """)
    void testCase1() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initPositiveDealResults(datasourceId);
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T2. TATN росла вчера, BRF4 падал вчера.
        Сигнал не зафиксирован.
        """)
    void testCase2() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initNegativeDeals(datasourceId);
        initNegativeDealResults(datasourceId);
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertFalse(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T3. С последнего запуска прошло меньше 24 часов, сканер не запущен.
        """)
    void testCase3() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initPositiveDealResults(datasourceId);
        initScanner(datasourceId);
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-22T18:00:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T4. С последнего запуска прошло 24 часа, сканер запущен.
        """)
    void testCase4() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initPositiveDealResults(datasourceId);
        initScanner(datasourceId);
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-23T13:00:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertFalse(getTatn().isRiseOvernight(stockOvernightScale));
        assertFalse(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T5. Исторические данные проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы за один день, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигнал зафиксирован.
        """)
    void testCase5() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initTradingResults(
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-21", 80D, 80D, 10D),
            buildDealResultBy(datasourceId, TATN, "2023-12-20", 251D, 252D, 1D, 1D),
            buildDealResultBy(datasourceId, TATN, "2023-12-21", 252D, 253D, 1D, 1D)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertEquals(1, scannerRepository().getAllBy(getDatasourceId()).get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T6. Исторические данные проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные не проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase6() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDealResults(datasourceId);
        initDealDatas(
            buildBuyDealBy(datasourceId, 1L, TATN, "10:00:00", 251.1D, 136926D, 1),
            buildBuyDealBy(datasourceId, 2L, TATN, "12:00:00", 247.1D, 136926D, 1),
            buildBuyDealBy(datasourceId, 3L, TATN, "13:45:00", 280.1D, 136926D, 1)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertFalse(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T7. Исторические данные не проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase7() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initTradingResults(
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-20", 75D, 75D, 10D),
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-21", 80D, 80D, 10D)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertFalse(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T8. Исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase8() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDealResults(datasourceId);
        initDealDatas(
            buildContractBy(datasourceId, 1L, BRF4, "10:00:00", 78D, 78000D, 1),
            buildContractBy(datasourceId, 2L, BRF4, "12:00:00", 96D, 96000D, 1)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertFalse(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T9. Исторические данные проинтегрированы за один день, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase9() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initTradingResults(
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-20", 75D, 75D, 10D),
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-21", 80D, 80D, 10D),
            buildDealResultBy(datasourceId, TATN, "2023-12-21", 252D, 253D, 1D, 1D)
        );
        initScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    private void initScanner(UUID datasourceId) {
        scannerManager().createScanner(
            CreateScannerCommand.builder()
                .workPeriodInMinutes(1)
                .description("Корреляция сектора с фьючерсом.")
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .properties(
                    SectoralFuturesProperties.builder()
                        .futuresOvernightScale(futuresOvernightScale)
                        .stockOvernightScale(stockOvernightScale)
                        .futuresTicker(BRF4)
                        .build()
                )
                .build()
        );
    }

    private void initPositiveDealResults(UUID datasourceId) {
        exchangeDataFixture().initTradingResults(
            List.of(
                buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-20", 75D, 75D, 10D),
                buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-21", 80D, 80D, 10D),
                buildDealResultBy(datasourceId, TATN, "2023-12-20", 251D, 252D, 1D, 1D),
                buildDealResultBy(datasourceId, TATN, "2023-12-21", 252D, 253D, 1D, 1D)
            )
        );
    }

    private void initNegativeDealResults(UUID datasourceId) {
        initTradingResults(
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-20", 75D, 75D, 10D),
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-21", 80D, 74D, 10D),
            buildDealResultBy(datasourceId, TATN, "2023-12-20", 251D, 252D, 1D, 1D),
            buildDealResultBy(datasourceId, TATN, "2023-12-21", 252D, 253D, 1D, 1D)
        );
    }

    private void initNegativeDeals(UUID datasourceId) {
        initDealDatas(
            buildContractBy(datasourceId, 1L, BRF4, "10:00:00", 73D, 73000D, 1),
            buildContractBy(datasourceId, 2L, BRF4, "12:00:00", 72D, 73000D, 1),
            buildBuyDealBy(datasourceId, 1L, TATN, "10:00:00", 251.1D, 136926D, 1),
            buildBuyDealBy(datasourceId, 2L, TATN, "12:00:00", 247.1D, 136926D, 1),
            buildBuyDealBy(datasourceId, 3L, TATN, "13:45:00", 280.1D, 136926D, 1)
        );
    }

    private void initPositiveDeals(UUID datasourceId) {
        initDealDatas(
            buildContractBy(datasourceId, 1L, BRF4, "10:00:00", 78D, 78000D, 1),
            buildContractBy(datasourceId, 2L, BRF4, "12:00:00", 96D, 96000D, 1),
            buildBuyDealBy(datasourceId, 1L, TATN, "10:00:00", 251.1D, 136926D, 1),
            buildBuyDealBy(datasourceId, 2L, TATN, "12:00:00", 247.1D, 136926D, 1),
            buildBuyDealBy(datasourceId, 3L, TATN, "13:45:00", 280.1D, 136926D, 1)
        );
    }

    private void initInstruments(UUID datasourceId) {
        exchangeDataFixture()
            .initInstruments(
                List.of(
                    tatn(),
                    brf4()
                )
            );
        datasourceManager().integrateInstruments(new IntegrateInstrumentsCommand(datasourceId));
        datasourceManager().enableUpdate(new EnableUpdateInstrumentsCommand(datasourceId, getTickers(datasourceId)));
    }
}
