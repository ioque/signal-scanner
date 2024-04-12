package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.configurator.command.SavePrefSimpleScanner;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SCANNER MANAGER TEST - SIMPLE-PREF PAIR ALGORITHM")
public class SimplePrefPairAlgoTest extends BaseScannerTest {
    private static final Double SPREAD_PARAM = 1.0;

    @Test
    @DisplayName("""
        T4. В текущих сделках Сбер и СберП разница между ценой последних сделок больше чем расчетный исторический показатель.
        Сигнал есть.
        """)
    void testCase4() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory(datasourceId);
        initPositiveDeals(datasourceId);
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertEquals(1D, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    private PrefSimplePair getPrefSimplePair() {
        return new PrefSimplePair(getSberp(), getSber());
    }


    @Test
    @DisplayName("""
        T5. В текущих сделках Сбер и СберП разница между ценой последних сделок меньше чем расчетный исторический показатель.
        Сигналов нет.
        """)
    void testCase5() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory(datasourceId);
        initNegativeDeals(datasourceId);
        initScanner(datasourceId, "SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.09999999999999432, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T6. С последнего запуска прошло меньше минуты, сканер не запущен.
        """)
    void testCase6() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory(datasourceId);
        initPositiveDeals(datasourceId);
        initScanner(datasourceId,"SBER", "SBERP");
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-21T11:00:30");

        datasourceManager().execute();

        assertEquals(3, getLogs().size());
        assertSignals(getSignals(), 1, 1, 0);
        assertEquals(1D, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T7. С последнего запуска прошла минута, сканер запущен.
        """)
    void testCase7() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory(datasourceId);
        initNegativeDeals(datasourceId);
        initScanner(datasourceId,"SBER", "SBERP");
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-21T11:01:00");

        datasourceManager().execute();

        assertEquals(6, getLogs().size());
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"SBER", "2023-12-14", 1D, 1D, 251.2, 1D),
            buildDealResultBy(datasourceId,"SBER", "2023-12-15", 1D, 1D, 252.2, 1D),
            buildDealResultBy(datasourceId,"SBERP", "2023-12-14", 1D, 1D, 251.2, 1D),
            buildDealResultBy(datasourceId,"SBERP", "2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(datasourceId,1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"SBER", "2023-12-14", 1D, 1D, 251.2, 1D),
            buildDealResultBy(datasourceId,"SBER", "2023-12-15", 1D, 1D, 252.2, 1D),
            buildDealResultBy(datasourceId,"SBERP", "2023-12-14", 1D, 1D, 251.2, 1D),
            buildDealResultBy(datasourceId,"SBERP", "2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(datasourceId,1L, "SBER", "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"SBERP", "2023-12-15", 1D, 1D, 251.2, 1D),
            buildDealResultBy(datasourceId,"SBERP", "2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(datasourceId, 1L, "SBER", "10:54:00", 250D, 136926D, 1),
            buildBuyDealBy(datasourceId, 1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"SBER", "2023-12-15", 1D, 1D, 251.2, 1D),
            buildDealResultBy(datasourceId,"SBER", "2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(datasourceId,1L, "SBER", "10:54:00", 250D, 136926D, 1),
            buildBuyDealBy(datasourceId,1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"SBERP", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy(datasourceId,"SBERP", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(datasourceId,1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy(datasourceId,"SBER", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(datasourceId,1L, "SBER", "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"SBERP", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy(datasourceId,"SBERP", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initDealDatas(
            buildBuyDealBy(datasourceId,1L, "SBER", "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy(datasourceId,"SBER", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initDealDatas(
            buildBuyDealBy(datasourceId,1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId,"SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initScanner(datasourceId, "SBER", "SBERP");

        datasourceManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    private void initScanner(UUID datasourceId, String... tickers) {
        scannerConfigurator().addNewScanner(
            SavePrefSimpleScanner.builder()
                .workPeriodInMinutes(1)
                .description("Анализ пар преф-обычка.")
                .datasourceId(datasourceId)
                .tickers(Arrays.asList(tickers))
                .spreadParam(SPREAD_PARAM)
                .build()
        );
    }

    private void initSberSberp(UUID datasourceId) {
        initInstruments(
            sber(),
            sberP()
        );
        datasourceManager().integrateInstruments(datasourceId);
        datasourceManager().enableUpdate(datasourceId, getTickers(datasourceId));
    }

    private void initSberAndSberpHistory(UUID datasourceId) {
        initTradingResults(
            buildDealResultBy(datasourceId, "SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy(datasourceId, "SBER", "2023-12-16", 1D, 1D, 260.58, 1D),
            buildDealResultBy(datasourceId, "SBER", "2023-12-17", 1D, 1D, 263.49, 1D),
            buildDealResultBy(datasourceId, "SBER", "2023-12-18", 1D, 1D, 268.47, 1D),
            buildDealResultBy(datasourceId, "SBER", "2023-12-19", 1D, 1D, 267.19, 1D),
            buildDealResultBy(datasourceId, "SBER", "2023-12-20", 1D, 1D, 267.89, 1D),
            buildDealResultBy(datasourceId, "SBER", "2023-12-21", 1D, 1D, 265.09, 1D),
            buildDealResultBy(datasourceId, "SBERP", "2023-12-15", 1D, 1D, 258.95, 1D),
            buildDealResultBy(datasourceId, "SBERP", "2023-12-16", 1D, 1D, 260.27, 1D),
            buildDealResultBy(datasourceId, "SBERP", "2023-12-17", 1D, 1D, 263.05, 1D),
            buildDealResultBy(datasourceId, "SBERP", "2023-12-18", 1D, 1D, 268.13, 1D),
            buildDealResultBy(datasourceId, "SBERP", "2023-12-19", 1D, 1D, 267.02, 1D),
            buildDealResultBy(datasourceId, "SBERP", "2023-12-20", 1D, 1D, 267.63, 1D),
            buildDealResultBy(datasourceId, "SBERP", "2023-12-21", 1D, 1D, 264.87, 1D)
        );
    }

    private void initNegativeDeals(UUID datasourceId) {
        initDealDatas(
            buildBuyDealBy(datasourceId, 1L, "SBER", "10:55:00", 250.1D, 136926D, 1),
            buildBuyDealBy(datasourceId, 1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
    }

    private void initPositiveDeals(UUID datasourceId) {
        initDealDatas(
            buildBuyDealBy(datasourceId, 1L, "SBER", "10:55:00", 251D, 136926D, 1),
            buildBuyDealBy(datasourceId, 1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
    }

    private List<ScannerLog> getLogs() {
        return scannerLogRepository().logs.get(scannerRepository().getAll().get(0).getId());
    }
}
