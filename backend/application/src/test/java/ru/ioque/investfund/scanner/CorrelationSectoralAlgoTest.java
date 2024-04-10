package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.configurator.entity.CorrelationSectoralAlgorithmConfig;
import ru.ioque.investfund.domain.core.DomainException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SIGNAL SCANNER MANAGER - CORRELATION SECTORAL ALGORITHM")
public class CorrelationSectoralAlgoTest extends BaseScannerTest {
    private static final double futuresOvernightScale = 0.02;
    private static final double stockOvernightScale = 0.005;
    private static final List<String> tickers = List.of(TATN, BRF4);
    private static final String startDate = "2023-12-22T13:00:00";

    @Test
    @DisplayName("""
        T1. В конфигурацию CorrelationSectoralSignalConfig не передан параметр futuresOvernightScale.
        Результат: ошибка, текст ошибки: "Не передан параметр futuresOvernightScale."
        """)
    void testCase1() {
        final UUID datasourceId = getDatasourceId();
        initInstruments(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                null,
                stockOvernightScale,
                BRF4
            )
        ));

        assertEquals("Не передан параметр futuresOvernightScale.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В конфигурацию CorrelationSectoralSignalConfig не передан параметр stockOvernightScale.
        Результат: ошибка, текст ошибки: "Не передан параметр stockOvernightScale."
        """)
    void testCase2() {
        final UUID datasourceId = getDatasourceId();
        initInstruments(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                futuresOvernightScale,
                null,
                BRF4
            )
        ));

        assertEquals("Не передан параметр stockOvernightScale.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В конфигурацию CorrelationSectoralSignalConfig не передан параметр futuresTicker.
        Результат: ошибка, текст ошибки: "Не передан параметр futuresTicker."
        """)
    void testCase3() {
        final UUID datasourceId = getDatasourceId();
        initInstruments(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                futuresOvernightScale,
                stockOvernightScale,
                null
            )
        ));

        assertEquals("Не передан параметр futuresTicker.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В конфигурацию CorrelationSectoralSignalConfig параметр futuresTicker передан как пустая строка.
        Результат: ошибка, текст ошибки: "Не передан параметр futuresTicker."
        """)
    void testCase4() {
        final UUID datasourceId = getDatasourceId();
        initInstruments(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                futuresOvernightScale,
                stockOvernightScale,
                ""
            )
        ));

        assertEquals("Не передан параметр futuresTicker.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T5. В конфигурацию CorrelationSectoralSignalConfig параметр futuresOvernightScale передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр futuresOvernightScale должен быть больше нуля."
        """)
    void testCase5() {
        final UUID datasourceId = getDatasourceId();
        initInstruments(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                0D,
                stockOvernightScale,
                BRF4
            )
        ));

        assertEquals("Параметр futuresOvernightScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T6. В конфигурацию CorrelationSectoralSignalConfig параметр futuresOvernightScale передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр futuresOvernightScale должен быть больше нуля."
        """)
    void testCase6() {
        final UUID datasourceId = getDatasourceId();
        initInstruments(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                -1D,
                stockOvernightScale,
                BRF4
            )
        ));

        assertEquals("Параметр futuresOvernightScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T7. В конфигурацию CorrelationSectoralSignalConfig параметр stockOvernightScale передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр stockOvernightScale должен быть больше нуля."
        """)
    void testCase7() {
        final UUID datasourceId = getDatasourceId();
        initInstruments(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                futuresOvernightScale,
                0D,
                BRF4
            )
        ));

        assertEquals("Параметр stockOvernightScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T8. В конфигурацию CorrelationSectoralSignalConfig параметр stockOvernightScale передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр stockOvernightScale должен быть больше нуля."
        """)
    void testCase8() {
        final UUID datasourceId = getDatasourceId();
        initInstruments(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                futuresOvernightScale,
                -1D,
                BRF4
            )
        ));

        assertEquals("Параметр stockOvernightScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T9. TATN росла вчера, BRF4 рос вчера.
        Сигнал зафиксирован.
        """)
    void testCase9() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initPositiveDealResults(datasourceId);
        initScanner(datasourceId);

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T10. TATN росла вчера, BRF4 падал вчера.
        Сигнал не зафиксирован.
        """)
    void testCase10() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initNegativeDeals(datasourceId);
        initNegativeDealResults(datasourceId);
        initScanner(datasourceId);

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertFalse(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T11. С последнего запуска прошло меньше 24 часов, сканер не запущен.
        """)
    void testCase11() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initPositiveDealResults(datasourceId);
        initScanner(datasourceId);
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-22T18:00:00");

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T12. С последнего запуска прошло 24 часа, сканер запущен.
        """)
    void testCase12() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initPositiveDealResults(datasourceId);
        initScanner(datasourceId);
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-23T13:00:00");

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertFalse(getTatn().isRiseOvernight(stockOvernightScale));
        assertFalse(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T13. Исторические данные проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы за один день, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигнал зафиксирован.
        """)
    void testCase13() {
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

        exchangeManager().execute();

        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T14. Исторические данные проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные не проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase14() {
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

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertFalse(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T15. Исторические данные не проинтегрированы, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase15() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDeals(datasourceId);
        initTradingResults(
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-20", 75D, 75D, 10D),
            buildFuturesDealResultBy(datasourceId, BRF4, "2023-12-21", 80D, 80D, 10D)
        );
        initScanner(datasourceId);

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFalse(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T16. Исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase16() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime(startDate);
        initInstruments(datasourceId);
        initPositiveDealResults(datasourceId);
        initDealDatas(
            buildContractBy(datasourceId, 1L, BRF4, "10:00:00", 78D, 78000D, 1),
            buildContractBy(datasourceId, 2L, BRF4, "12:00:00", 96D, 96000D, 1)
        );
        initScanner(datasourceId);

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFalse(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    @Test
    @DisplayName("""
        T17. Исторические данные проинтегрированы за один день, внутридневные данные проинтегрированы, цена растет.
        Исторические данные по фьючерсу проинтегрированы, дневные данные проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase17() {
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

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getTatn().isRiseOvernight(stockOvernightScale));
        assertTrue(getBrf4().isRiseOvernight(futuresOvernightScale));
    }

    private void initScanner(UUID datasourceId) {
        addScanner(
            1,
            "Корреляция сектора с фьючерсом.",
            datasourceId,
            getTickers(datasourceId),
            new CorrelationSectoralAlgorithmConfig(
                futuresOvernightScale,
                stockOvernightScale,
                BRF4
            )
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
        exchangeManager().integrateInstruments(datasourceId);
        exchangeManager().enableUpdate(datasourceId, tickers);
    }
}
