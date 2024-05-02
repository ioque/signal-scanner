package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.application.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.value.PrefSimplePair;

import java.util.Arrays;

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
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory();
        initPositiveDeals();
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertEquals(1D, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    private PrefSimplePair getPrefSimplePair() {
        return new PrefSimplePair(getSberpSnapshot(), getSberSnapshot());
    }


    @Test
    @DisplayName("""
        T5. В текущих сделках Сбер и СберП разница между ценой последних сделок меньше чем расчетный исторический показатель.
        Сигналов нет.
        """)
    void testCase5() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory();
        initNegativeDeals();
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.09999999999999432, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T6. С последнего запуска прошло меньше минуты, сканер не запущен.
        """)
    void testCase6() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory();
        initPositiveDeals();
        initScanner(datasourceId, SBER, SBERP);
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-21T11:00:30");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1, 0);
        assertEquals(1D, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.2599999999999909, getPrefSimplePair().getHistoryDelta());
    }

    @Test
    @DisplayName("""
        T7. С последнего запуска прошла минута, сканер запущен.
        """)
    void testCase7() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-21T11:00:00");
        initSberSberp(datasourceId);
        initSberAndSberpHistory();
        initNegativeDeals();
        initScanner(datasourceId, SBER, SBERP);
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-21T11:01:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(),0, 0, 0);
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
        initHistoryValues(
            buildSberHistoryValue("2023-12-14", 1D, 1D, 251.2, 1D),
            buildSberHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D),
            buildSberpHistoryValue("2023-12-14", 1D, 1D, 251.2, 1D),
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initIntradayValues(
            buildSberpBuyDeal(1L,"10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initHistoryValues(
            buildSberHistoryValue("2023-12-14", 1D, 1D, 251.2, 1D),
            buildSberHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D),
            buildSberpHistoryValue("2023-12-14", 1D, 1D, 251.2, 1D),
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initIntradayValues(
            buildSberBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initHistoryValues(
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 251.2, 1D),
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initIntradayValues(
            buildSberBuyDeal(1L, "10:54:00", 250D, 136926D, 1),
            buildSberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initHistoryValues(
            buildSberHistoryValue("2023-12-15", 1D, 1D, 251.2, 1D),
            buildSberHistoryValue("2023-12-15", 1D, 1D, 252.2, 1D)
        );
        initIntradayValues(
            buildSberBuyDeal(1L, "10:54:00", 250D, 136926D, 1),
            buildSberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initHistoryValues(
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initIntradayValues(
            buildSberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initHistoryValues(
            buildSberHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            buildSberHistoryValue("2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initIntradayValues(
            buildSberBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initHistoryValues(
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initIntradayValues(
            buildSberBuyDeal(1L,"10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initHistoryValues(
            buildSberHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            buildSberHistoryValue("2023-12-15", 1D, 1D, 254.2, 1D)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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
        initIntradayValues(
            buildSberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
        initScanner(datasourceId, SBER, SBERP);

        runWorkPipeline(datasourceId);

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

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0, 0);
        assertEquals(0.0, getPrefSimplePair().getCurrentDelta());
        assertEquals(0.0, getPrefSimplePair().getHistoryDelta());
    }

    private void initScanner(DatasourceId datasourceId, String... tickers) {
        commandBus().execute(
            CreateScannerCommand.builder()
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
            sber(),
            sberp()
        );
        commandBus().execute(new IntegrateInstrumentsCommand(datasourceId));
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, getTickers(datasourceId)));
    }

    private void initSberAndSberpHistory() {
        initHistoryValues(
            buildSberHistoryValue("2023-12-15", 1D, 1D, 259.2, 1D),
            buildSberHistoryValue("2023-12-16", 1D, 1D, 260.58, 1D),
            buildSberHistoryValue("2023-12-17", 1D, 1D, 263.49, 1D),
            buildSberHistoryValue("2023-12-18", 1D, 1D, 268.47, 1D),
            buildSberHistoryValue("2023-12-19", 1D, 1D, 267.19, 1D),
            buildSberHistoryValue("2023-12-20", 1D, 1D, 267.89, 1D),
            buildSberHistoryValue("2023-12-21", 1D, 1D, 265.09, 1D),
            buildSberpHistoryValue("2023-12-15", 1D, 1D, 258.95, 1D),
            buildSberpHistoryValue("2023-12-16", 1D, 1D, 260.27, 1D),
            buildSberpHistoryValue("2023-12-17", 1D, 1D, 263.05, 1D),
            buildSberpHistoryValue("2023-12-18", 1D, 1D, 268.13, 1D),
            buildSberpHistoryValue("2023-12-19", 1D, 1D, 267.02, 1D),
            buildSberpHistoryValue("2023-12-20", 1D, 1D, 267.63, 1D),
            buildSberpHistoryValue("2023-12-21", 1D, 1D, 264.87, 1D)
        );
    }

    private void initNegativeDeals() {
        initIntradayValues(
            buildSberBuyDeal(1L,  "10:55:00", 250.1D, 136926D, 1),
            buildSberpBuyDeal(1L, "10:54:00", 250D, 136926D, 1)
        );
    }

    private void initPositiveDeals() {
        initIntradayValues(
            buildSberBuyDeal(1L,"10:55:00", 251D, 136926D, 1),
            buildSberpBuyDeal(1L,"10:54:00", 250D, 136926D, 1)
        );
    }
}
