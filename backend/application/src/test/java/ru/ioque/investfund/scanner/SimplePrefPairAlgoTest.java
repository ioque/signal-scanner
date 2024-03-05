package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.financial.algorithms.PrefSimpleSignalConfig;

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
            new PrefSimpleSignalConfig(null),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new PrefSimpleSignalConfig(0D),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new PrefSimpleSignalConfig(-1D),
            getInstruments().stream().map(Instrument::getId).toList()
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
        initSberSberPSecurityAndHistoryTradingData();
        initPositiveDeals();

        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        scheduleManager().executeSchedule();

        var instruments = getInstruments();
        var sber = instruments.stream().filter(row -> row.getTicker().equals("SBER")).findFirst().orElseThrow();
        var sberp = instruments.stream().filter(row -> row.getTicker().equals("SBERP")).findFirst().orElseThrow();

        assertEquals(7, sber.getDailyValues().size());
        assertEquals(7, sberp.getDailyValues().size());
        assertEquals(1, sber.getIntradayValues().size());
        assertEquals(1, sberp.getIntradayValues().size());

        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(1.0),
            getInstrumentsBy(tickers)
                .map(Instrument::getId)
                .toList()
        );
        scheduleManager().executeSchedule();
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
        initSberSberPSecurityAndHistoryTradingData();
        initNegativeDeals();

        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        scheduleManager().executeSchedule();

        var instruments = getInstruments();
        var sber = instruments.stream().filter(row -> row.getTicker().equals("SBER")).findFirst().orElseThrow();
        var sberp = instruments.stream().filter(row -> row.getTicker().equals("SBERP")).findFirst().orElseThrow();

        assertEquals(7, sber.getDailyValues().size());
        assertEquals(7, sberp.getDailyValues().size());
        assertEquals(1, sber.getIntradayValues().size());
        assertEquals(1, sberp.getIntradayValues().size());

        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(1.0),
            getInstrumentsBy(tickers).map(Instrument::getId).toList()
        );
        scheduleManager().executeSchedule();
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T6. С последнего запуска прошло меньше минуты, сканер не запущен.
        """)
    void testCase6() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberPSecurityAndHistoryTradingData();
        initNegativeDeals();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(1.0),
            getInstrumentsBy(tickers).map(Instrument::getId).toList()
        );
        scheduleManager().executeSchedule();
        clearLogs();
        initTodayDateTime("2023-12-21T11:00:30");
        scheduleManager().executeSchedule();
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T7. С последнего запуска прошла минута, сканер запущен.
        """)
    void testCase7() {
        final var tickers = List.of("SBER", "SBERP");
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberPSecurityAndHistoryTradingData();
        initNegativeDeals();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Анализ пар преф-обычка.",
            new PrefSimpleSignalConfig(1.0),
            getInstrumentsBy(tickers).map(Instrument::getId).toList()
        );
        scheduleManager().executeSchedule();
        clearLogs();
        initTodayDateTime("2023-12-21T11:01:00");
        scheduleManager().executeSchedule();
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
        assertEquals(6, scannerLogRepository().logs.get(fakeDataScannerStorage().getAll().get(0).getId()).size());
    }

    private void initSberSberPSecurityAndHistoryTradingData() {
        initInstruments(
            imoex(),
            brf4(),
            usdRub(),
            sber(),
            sberP()
        );
        initTradingResults(
            buildDealResultBy("SBER", "2023-12-15", 1D, 1D, 259.2, 1D),
            buildDealResultBy("SBER", "2023-12-16",  1D,1D, 260.58, 1D),
            buildDealResultBy("SBER", "2023-12-17", 1D, 1D, 263.49, 1D),
            buildDealResultBy("SBER", "2023-12-18",  1D,1D, 268.47, 1D),
            buildDealResultBy("SBER", "2023-12-19",  1D,1D, 267.19, 1D),
            buildDealResultBy("SBER", "2023-12-20",  1D,1D, 267.89, 1D),
            buildDealResultBy("SBER", "2023-12-21", 1D, 1D, 265.09, 1D),
            buildDealResultBy("SBERP", "2023-12-15", 1D, 1D, 258.95, 1D),
            buildDealResultBy("SBERP", "2023-12-16",  1D,1D, 260.27, 1D),
            buildDealResultBy("SBERP", "2023-12-17",  1D,1D, 263.05, 1D),
            buildDealResultBy("SBERP", "2023-12-18",  1D,1D, 268.13, 1D),
            buildDealResultBy("SBERP", "2023-12-19",  1D,1D, 267.02, 1D),
            buildDealResultBy("SBERP", "2023-12-20", 1D, 1D, 267.63, 1D),
            buildDealResultBy("SBERP", "2023-12-21",  1D,1D, 264.87, 1D)
        );
    }

    private void initNegativeDeals() {
        initDealDatas(
            buildDealBy(1L, "SBER", "10:55:00", 250.1D,136926D, 1),
            buildDealBy(1L, "SBERP", "10:54:00", 250D,136926D, 1)
        );
    }

    private void initPositiveDeals() {
        initDealDatas(
            buildDealBy(1L, "SBER", "10:55:00", 251D,136926D, 1),
            buildDealBy(1L, "SBERP", "10:54:00", 250D,136926D, 1)
        );
    }
}
