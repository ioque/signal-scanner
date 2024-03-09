package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.algorithms.CorrelationSectoralSignalConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SIGNAL SCANNER MANAGER - CORRELATION SECTORAL ALGORITHM")
public class CorrelationSectoralAlgoTest extends BaseScannerTest {
    @Test
    @DisplayName("""
        T1. В конфигурацию CorrelationSectoralSignalConfig не передан параметр futuresOvernightScale.
        Результат: ошибка, текст ошибки: "Не передан параметр futuresOvernightScale."
        """)
    void testCase1() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), null, 1D, "BRF4")
        ));
        assertEquals("Не передан параметр futuresOvernightScale.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В конфигурацию CorrelationSectoralSignalConfig не передан параметр stockOvernightScale.
        Результат: ошибка, текст ошибки: "Не передан параметр stockOvernightScale."
        """)
    void testCase2() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 1.5, null, "BRF4")
        ));
        assertEquals("Не передан параметр stockOvernightScale.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В конфигурацию CorrelationSectoralSignalConfig не передан параметр futuresTicker.
        Результат: ошибка, текст ошибки: "Не передан параметр futuresTicker."
        """)
    void testCase3() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 1.5, 1D, null)
        ));
        assertEquals("Не передан параметр futuresTicker.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В конфигурацию CorrelationSectoralSignalConfig параметр futuresTicker передан как пустая строка.
        Результат: ошибка, текст ошибки: "Не передан параметр futuresTicker."
        """)
    void testCase4() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 1.5, 1D, "")
        ));
        assertEquals("Не передан параметр futuresTicker.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T5. В конфигурацию CorrelationSectoralSignalConfig параметр futuresOvernightScale передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр futuresOvernightScale должен быть больше нуля."
        """)
    void testCase5() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0D, 1D, "BRF4")
        ));
        assertEquals("Параметр futuresOvernightScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T6. В конфигурацию CorrelationSectoralSignalConfig параметр futuresOvernightScale передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр futuresOvernightScale должен быть больше нуля."
        """)
    void testCase6() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), -1D, 1D, "BRF4")
        ));
        assertEquals("Параметр futuresOvernightScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T7. В конфигурацию CorrelationSectoralSignalConfig параметр stockOvernightScale передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр stockOvernightScale должен быть больше нуля."
        """)
    void testCase7() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 1.5, 0D, "BRF4")
        ));
        assertEquals("Параметр stockOvernightScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T8. В конфигурацию CorrelationSectoralSignalConfig параметр stockOvernightScale передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр stockOvernightScale должен быть больше нуля."
        """)
    void testCase8() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 1.5, -1D, "BRF4")
        ));
        assertEquals("Параметр stockOvernightScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T9. TATN росла вчера, BRF4 рос вчера.
        Сигнал зафиксирован.
        """)
    void testCase9() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initPositiveDeals();
        initPositiveDealResults();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );

        runWorkPipline();

        assertEquals(2, getTatn().getDailyValues().size());
        assertEquals(3, getTatn().getIntradayValues().size());
        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T10. TATN росла вчера, BRF4 падал вчера.
        Сигнал не зафиксирован.
        """)
    void testCase27() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initNegativeDeals();
        initNegativeDealResults();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );

        runWorkPipline();

        assertEquals(2, getTatn().getDailyValues().size());
        assertEquals(3, getTatn().getIntradayValues().size());
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T11. С последнего запуска прошло меньше 24 часов, сканер не запущен.
        """)
    void testCase11() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initPositiveDeals();
        initPositiveDealResults();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );
        loggerProvider().clearLogs();
        runWorkPipline();
        initTodayDateTime("2023-12-22T18:00:00");

        runWorkPipline();

        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T12. С последнего запуска прошло 24 часа, сканер запущен.
        """)
    void testCase12() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initPositiveDeals();
        initPositiveDealResults();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );
        loggerProvider().clearLogs();
        runWorkPipline();
        initTodayDateTime("2023-12-23T13:00:00");

        runWorkPipline();

        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T13. Исторические данные проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы за один день, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигнал зафиксирован.
        """)
    void testCase13() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initPositiveDeals();
        exchangeDataFixture().initTradingResults(
            List.of(
                buildFuturesDealResultBy("BRF4", "2023-12-21", 80D, 80D, 10D, 1),
                buildDealResultBy("TATN", "2023-12-20", 251D, 252D, 1D, 1D),
                buildDealResultBy("TATN", "2023-12-21", 252D, 253D, 1D, 1D)
            )
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );
        runWorkPipline();
        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T14. Исторические данные проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные не проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase14() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initPositiveDealResults();
        exchangeDataFixture().initDealDatas(
            List.of(
                buildBuyDealBy(1L, "TATN", "10:00:00", 251.1D, 136926D, 1),
                buildBuyDealBy(2L, "TATN", "12:00:00", 247.1D, 136926D, 1),
                buildBuyDealBy(3L, "TATN", "13:45:00", 280.1D, 136926D, 1)
            )
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );
        runWorkPipline();
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T15. Исторические данные не проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase15() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initPositiveDeals();
        exchangeDataFixture().initTradingResults(
            List.of(
                buildFuturesDealResultBy("BRF4", "2023-12-20", 75D, 75D, 10D, 1),
                buildFuturesDealResultBy("BRF4", "2023-12-21", 80D, 80D, 10D, 1)
            )
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );
        runWorkPipline();
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T16. Исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase16() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initPositiveDealResults();
        exchangeDataFixture().initDealDatas(
            List.of(
                buildFuturesDealBy(1L, "BRF4", "10:00:00", 78D, 78000D, 1),
                buildFuturesDealBy(2L, "BRF4", "12:00:00", 96D, 96000D, 1)
            )
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );
        runWorkPipline();
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T17. Исторические данные проинтегрированы за один день, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase17() {
        final var tickers = List.of("TATN", "BRF4");
        initTodayDateTime("2023-12-22T13:00:00");
        initInstruments();
        initPositiveDeals();
        exchangeDataFixture().initTradingResults(
            List.of(
                buildFuturesDealResultBy("BRF4", "2023-12-20", 75D, 75D, 10D, 1),
                buildFuturesDealResultBy("BRF4", "2023-12-21", 80D, 80D, 10D, 1),
                buildDealResultBy("TATN", "2023-12-21", 252D, 253D, 1D, 1D)
            )
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Корреляция сектора с фьючерсом.",
            new CorrelationSectoralSignalConfig(getInstrumentIds(), 0.02, 0.005, "BRF4")
        );

        runWorkPipline();

        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    private void initPositiveDealResults() {
        exchangeDataFixture().initTradingResults(
            List.of(
                buildFuturesDealResultBy("BRF4", "2023-12-20", 75D, 75D, 10D, 1),
                buildFuturesDealResultBy("BRF4", "2023-12-21", 80D, 80D, 10D, 1),
                buildDealResultBy("TATN", "2023-12-20", 251D, 252D, 1D, 1D),
                buildDealResultBy("TATN", "2023-12-21", 252D, 253D, 1D, 1D)
            )
        );
    }

    private Instrument getTatn() {
        return getInstruments().stream().filter(row -> row.getTicker().equals("TATN")).findFirst().orElseThrow();
    }

    private void initNegativeDealResults() {
        exchangeDataFixture().initTradingResults(
            List.of(
                buildFuturesDealResultBy("BRF4", "2023-12-20", 75D, 75D, 10D, 1),
                buildFuturesDealResultBy("BRF4", "2023-12-21", 80D, 74D, 10D, 1),
                buildDealResultBy("TATN", "2023-12-20", 251D, 252D, 1D, 1D),
                buildDealResultBy("TATN", "2023-12-21", 252D, 253D, 1D, 1D)
            )
        );
    }

    private void initNegativeDeals() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildFuturesDealBy(1L, "BRF4", "10:00:00", 73D, 73000D, 1),
                buildFuturesDealBy(2L, "BRF4", "12:00:00", 72D, 73000D, 1),
                buildBuyDealBy(1L, "TATN", "10:00:00", 251.1D, 136926D, 1),
                buildBuyDealBy(2L, "TATN", "12:00:00", 247.1D, 136926D, 1),
                buildBuyDealBy(3L, "TATN", "13:45:00", 280.1D, 136926D, 1)
            )
        );
    }

    private void initPositiveDeals() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildFuturesDealBy(1L, "BRF4", "10:00:00", 78D, 78000D, 1),
                buildFuturesDealBy(2L, "BRF4", "12:00:00", 96D, 96000D, 1),
                buildBuyDealBy(1L, "TATN", "10:00:00", 251.1D, 136926D, 1),
                buildBuyDealBy(2L, "TATN", "12:00:00", 247.1D, 136926D, 1),
                buildBuyDealBy(3L, "TATN", "13:45:00", 280.1D, 136926D, 1)
            )
        );
    }

    private void initInstruments() {
        exchangeDataFixture()
            .initInstruments(
                List.of(
                    tatn(),
                    brf4()
                )
            );
    }
}
