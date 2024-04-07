package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.algorithms.prefsimplepair.PrefSimpleAlgorithmConfig;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;
import ru.ioque.investfund.domain.scanner.value.ScannerLog;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SIGNAL SCANNER MANAGER - SIMPLE-PREF PAIR ALGORITHM")
public class SimplePrefPairAlgoTest extends BaseScannerTest {
    private static final Double SPREAD_PARAM = 1.0;
    @Test
    @DisplayName("""
        T1. В конфигурацию PrefSimpleSignalConfig не передан параметр spreadParam.
        Результат: ошибка, текст ошибки: "Не передан параметр spreadParam."
        """)
    void testCase1() {
        initSberSberp();

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Анализ пар преф-обычка.",
            getTickers(),
            new PrefSimpleAlgorithmConfig(null)
        ));

        assertEquals("Не передан параметр spreadParam.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В конфигурацию PrefSimpleSignalConfig параметр spreadParam передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр spreadParam должен быть больше нуля."
        """)
    void testCase2() {
        initSberSberp();

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Анализ пар преф-обычка.",
            getTickers(),
            new PrefSimpleAlgorithmConfig(0D)
        ));

        assertEquals("Параметр spreadParam должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В конфигурацию PrefSimpleSignalConfig параметр spreadParam передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр spreadParam должен быть больше нуля."
        """)
    void testCase3() {
        initSberSberp();

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Анализ пар преф-обычка.",
            getTickers(),
            new PrefSimpleAlgorithmConfig(-1D)
        ));

        assertEquals("Параметр spreadParam должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В текущих сделках Сбер и СберП разница между ценой последних сделок больше чем расчетный исторический показатель.
        Сигнал есть.
        """)
    void testCase4() {
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initSberAndSberpHistory();
        initPositiveDeals();
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initSberAndSberpHistory();
        initNegativeDeals();
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.09999999999999432, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T6. С последнего запуска прошло меньше минуты, сканер не запущен.
        """)
    void testCase6() {
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initSberAndSberpHistory();
        initPositiveDeals();
        initScanner("SBER", "SBERP");
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-21T11:00:30");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initSberAndSberpHistory();
        initNegativeDeals();
        initScanner("SBER", "SBERP");
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-21T11:01:00");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 251.2, 1D),
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 252.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 251.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 251.2, 1D),
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 252.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 251.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:54:00", 250D, 136926D, 1)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 251.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:54:00", 250D, 136926D, 1),
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 251.2, 1D),
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:54:00", 250D, 136926D, 1),
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:54:00", 250D, 136926D, 1)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:54:00", 250D, 136926D, 1)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initDealDatas(
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

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
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initScanner("SBER", "SBERP");

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    private void initScanner(String... tickers) {
        addScanner(
            1,
            "Анализ пар преф-обычка.",
            getInstrumentsBy(Arrays.asList(tickers)).map(Instrument::getTicker).toList(),
            new PrefSimpleAlgorithmConfig(SPREAD_PARAM)
        );
    }

    private void initSberSberp() {
        initInstruments(
            sber(),
            sberP()
        );
        exchangeManager().integrateInstruments();
        exchangeManager().enableUpdate(getTickers());
    }

    private void initSberAndSberpHistory() {
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBER", "2023-12-16", 1D, 1D, 260.58, 1D),
            buildDealResultBy("SBER", "2023-12-17", 1D, 1D, 263.49, 1D),
            buildDealResultBy("SBER", "2023-12-18", 1D, 1D, 268.47, 1D),
            buildDealResultBy("SBER", "2023-12-19", 1D, 1D, 267.19, 1D),
            buildDealResultBy("SBER", "2023-12-20", 1D, 1D, 267.89, 1D),
            buildDealResultBy("SBER", "2023-12-21", 1D, 1D, 265.09, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 258.95, 1D),
            buildDealResultBy("SBERP", "2023-12-16", 1D, 1D, 260.27, 1D),
            buildDealResultBy("SBERP", "2023-12-17", 1D, 1D, 263.05, 1D),
            buildDealResultBy("SBERP", "2023-12-18", 1D, 1D, 268.13, 1D),
            buildDealResultBy("SBERP", "2023-12-19", 1D, 1D, 267.02, 1D),
            buildDealResultBy("SBERP", "2023-12-20", 1D, 1D, 267.63, 1D),
            buildDealResultBy("SBERP", "2023-12-21", 1D, 1D, 264.87, 1D)
        );
    }

    private void initNegativeDeals() {
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:55:00", 250.1D, 136926D, 1),
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
    }

    private void initPositiveDeals() {
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:55:00", 251D, 136926D, 1),
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
    }

    private List<ScannerLog> getLogs() {
        return scannerLogRepository().logs.get(fakeDataScannerStorage().getAll().get(0).getId());
    }
}
