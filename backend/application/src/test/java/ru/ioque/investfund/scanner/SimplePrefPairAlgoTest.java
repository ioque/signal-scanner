package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.algorithms.PrefSimpleSignalConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SIGNAL SCANNER MANAGER - SIMPLE-PREF PAIR ALGORITHM")
public class SimplePrefPairAlgoTest extends BaseScannerTest {
    @Test
    @DisplayName("""
        T1. В конфигурацию PrefSimpleSignalConfig не передан параметр spreadParam.
        Результат: ошибка, текст ошибки: "Не передан параметр spreadParam."
        """)
    void testCase1() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentIds(), null)
        ));
        assertEquals("Не передан параметр spreadParam.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В конфигурацию PrefSimpleSignalConfig параметр spreadParam передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр spreadParam должен быть больше нуля."
        """)
    void testCase2() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentIds(), 0D)
        ));
        assertEquals("Параметр spreadParam должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В конфигурацию PrefSimpleSignalConfig параметр spreadParam передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр spreadParam должен быть больше нуля."
        """)
    void testCase3() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentIds(), -1D)
        ));
        assertEquals("Параметр spreadParam должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В текущих сделках Сбер и СберП разница между ценой последних сделок больше чем расчетный исторический показатель.
        Сигнал есть.
        """)
    void testCase4() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initSberSberPSecurityAndHistoryTradingData();
        initPositiveDeals();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );

        runWorkPipline();

        var instruments = getInstruments();
        var sber = instruments.stream().filter(row -> row.getTicker().equals("SBER")).findFirst().orElseThrow();
        var sberp = instruments.stream().filter(row -> row.getTicker().equals("SBERP")).findFirst().orElseThrow();
        assertEquals(7, sber.getDailyValues().size());
        assertEquals(7, sberp.getDailyValues().size());
        assertEquals(1, sber.getIntradayValues().size());
        assertEquals(1, sberp.getIntradayValues().size());
        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T5. В текущих сделках Сбер и СберП разница между ценой последних сделок меньше чем расчетный исторический показатель.
        Сигналов нет.
        """)
    void testCase5() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initSberSberPSecurityAndHistoryTradingData();
        initNegativeDeals();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );

        runWorkPipline();

        var instruments = getInstruments();
        var sber = instruments.stream().filter(row -> row.getTicker().equals("SBER")).findFirst().orElseThrow();
        var sberp = instruments.stream().filter(row -> row.getTicker().equals("SBERP")).findFirst().orElseThrow();
        assertEquals(7, sber.getDailyValues().size());
        assertEquals(7, sberp.getDailyValues().size());
        assertEquals(1, sber.getIntradayValues().size());
        assertEquals(1, sberp.getIntradayValues().size());
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T6. С последнего запуска прошло меньше минуты, сканер не запущен.
        """)
    void testCase6() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initSberSberPSecurityAndHistoryTradingData();
        initNegativeDeals();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();
        clearLogs();
        initTodayDateTime("2023-12-21T11:00:30");
        runWorkPipline();
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T7. С последнего запуска прошла минута, сканер запущен.
        """)
    void testCase7() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initSberSberPSecurityAndHistoryTradingData();
        initNegativeDeals();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();
        clearLogs();
        initTodayDateTime("2023-12-21T11:01:00");
        runWorkPipline();
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
        assertEquals(6, scannerLogRepository().logs.get(fakeDataScannerStorage().getAll().get(0).getId()).size());
    }

    @Test
    @DisplayName("""
        T8. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase8() {
        final var tickers = List.of("SBER", "SBERP");
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
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T9. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase9() {
        final var tickers = List.of("SBER", "SBERP");
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
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T10. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase10() {
        final var tickers = List.of("SBER", "SBERP");
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
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T11. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase11() {
        final var tickers = List.of("SBER", "SBERP");
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
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T12. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase12() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T13. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase13() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:54:00", 250D, 136926D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T14. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase14() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T15. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase15() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initDealDatas(
            buildBuyDealBy(1L, "SBER", "10:54:00", 250D, 136926D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T16. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase16() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 254.2, 1D)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T17. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase17() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        initDealDatas(
            buildBuyDealBy(1L, "SBERP", "10:54:00", 250D, 136926D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T18. Добавлен сканер для SBER, SBERP.
        SBER: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        SBERP: исторические данные не проинтегрированы, внутридневные данные не проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase18() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.0)
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    private void initSberSberp() {
        initInstruments(
            sber(),
            sberP()
        );
    }

    private void initSberSberPSecurityAndHistoryTradingData() {
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
}
