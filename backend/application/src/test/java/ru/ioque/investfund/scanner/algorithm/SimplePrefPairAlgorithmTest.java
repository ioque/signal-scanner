package ru.ioque.investfund.scanner.algorithm;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.algorithms.impl.PrefSimplePair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.SBER;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.SBERP;

@DisplayName("SIMPLE PREF PAIR ALGORITHM TEST")
public class SimplePrefPairAlgorithmTest extends BaseAlgorithmTest {
    private static final Double SPREAD_PARAM = 1.0;

    @Test
    @DisplayName("""
        T4. В текущих сделках Сбер и СберП разница между ценой последних сделок больше чем расчетный исторический показатель.
        Сигнал есть.
        """)
    void testCase4() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory();
        initPositiveDeals();
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertEquals(1D, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    private PrefSimplePair getPrefSimplePair() {
        return new PrefSimplePair(getSberpPerformance(), getSberPerformance());
    }


    @Test
    @DisplayName("""
        T5. В текущих сделках Сбер и СберП разница между ценой последних сделок меньше чем расчетный исторический показатель.
        Сигналов нет.
        """)
    void testCase5() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory();
        initNegativeDeals();
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.09999999999999432, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T8. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase8() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initAggregatedTotals(
            historyFixture.sberHistoryValue("2023-12-14", 1D, 1D, 251.2, 1D),
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D),
            historyFixture.sberpHistoryValue("2023-12-14", 1D, 1D, 251.2, 1D),
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initIntradayData(
            intradayFixture.sberpBuyDeal(1L,"10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T9. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase9() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initAggregatedTotals(
            historyFixture.sberHistoryValue("2023-12-14", 1D, 1D, 251.2, 1D),
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D),
            historyFixture.sberpHistoryValue("2023-12-14", 1D, 1D, 251.2, 1D),
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initIntradayData(
            intradayFixture.sberBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T10. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase10() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initAggregatedTotals(
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 251.2, 1D),
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initIntradayData(
            intradayFixture.sberBuyDeal(1L, "10:54:00", 250D, 136926D, 1),
            intradayFixture.sberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T11. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase11() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initAggregatedTotals(
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 251.2, 1D),
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initIntradayData(
            intradayFixture.sberBuyDeal(1L, "10:54:00", 250D, 136926D, 1),
            intradayFixture.sberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T12. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase12() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initAggregatedTotals(
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initIntradayData(
            intradayFixture.sberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T13. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase13() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initAggregatedTotals(
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initIntradayData(
            intradayFixture.sberBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T14. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase14() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initAggregatedTotals(
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T15. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase15() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initIntradayData(
            intradayFixture.sberBuyDeal(1L,"10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T16. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase16() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initAggregatedTotals(
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T17. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase17() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initIntradayData(
            intradayFixture.sberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T18. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase18() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initScanner(datasourceId, SBER, SBERP);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    private void initScanner(DatasourceId datasourceId, String... tickers) {
        commandBus().execute(
            CreateScanner.builder()
                .workPeriodInMinutes(1)
                .description("Анализ пар преф-обычка.")
                .datasourceId(datasourceId)
                .tickers(Arrays.stream(tickers).map(Ticker::from).toList())
                .properties(
                    PrefCommonProperties.builder()
                        .spreadValue(SPREAD_PARAM)
                        .build()
                )
                .build()
        );
    }

    private void initSberSberp(DatasourceId datasourceId) {
        initInstrumentDetails(
            instrumentFixture.sber(),
            instrumentFixture.sberp()
        );
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(new EnableUpdateInstruments(datasourceId, getTickers(datasourceId)));
    }

    private void initSberAndSberpHistory() {
        initAggregatedTotals(
            historyFixture.sberHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            historyFixture.sberHistoryValue("2023-12-16", 1D, 1D, 260.58, 1D),
            historyFixture.sberHistoryValue("2023-12-17", 1D, 1D, 263.49, 1D),
            historyFixture.sberHistoryValue("2023-12-18", 1D, 1D, 268.47, 1D),
            historyFixture.sberHistoryValue("2023-12-19", 1D, 1D, 267.19, 1D),
            historyFixture.sberHistoryValue("2023-12-20", 1D, 1D, 267.89, 1D),
            historyFixture.sberHistoryValue("2023-12-21", 1D, 1D, 265.09, 1D),
            historyFixture.sberpHistoryValue("2023-12-15", 1D, 1D, 258.95, 1D),
            historyFixture.sberpHistoryValue("2023-12-16", 1D, 1D, 260.27, 1D),
            historyFixture.sberpHistoryValue("2023-12-17", 1D, 1D, 263.05, 1D),
            historyFixture.sberpHistoryValue("2023-12-18", 1D, 1D, 268.13, 1D),
            historyFixture.sberpHistoryValue("2023-12-19", 1D, 1D, 267.02, 1D),
            historyFixture.sberpHistoryValue("2023-12-20", 1D, 1D, 267.63, 1D),
            historyFixture.sberpHistoryValue("2023-12-21", 1D, 1D, 264.87, 1D)
        );
    }

    private void initNegativeDeals() {
        initIntradayData(
            intradayFixture.sberBuyDeal(1L,  "10:55:00", 250.1D, 136926D, 1),
            intradayFixture.sberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
    }

    private void initPositiveDeals() {
        initIntradayData(
            intradayFixture.sberBuyDeal(1L,"10:55:00", 251D, 136926D, 1),
            intradayFixture.sberpBuyDeal(1L,"10:54:00", 250D, 136926D, 1)
        );
    }
}