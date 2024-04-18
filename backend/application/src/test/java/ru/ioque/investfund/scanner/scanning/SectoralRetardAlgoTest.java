package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANNER MANAGER TEST - SECTORAL RETARD ALGORITHM")
public class SectoralRetardAlgoTest extends BaseScannerTest {
    private final Double historyScale = 0.015;
    private final Double intradayScale = 0.015;

    @Test
    @DisplayName("""
        T1. ROSN, LKOH, SIBN, TATN - сектор нефти.
        3 из 4 позиций не росли в последние дни, сигнала нет.
        """)
    void testCase1() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase1(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "LKOH", "SIBN", "TATN");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertFalse(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getLkoh().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T2. ROSN, LKOH, SIBN росли в предыдущий день, растут сегодня.
        TATN росла вчера, сегодня падает.
        """)
    void testCase2() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "LKOH", "SIBN", "TATN");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1,1, 0);
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkoh().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T3. С последнего запуска прошло меньше 1 часа, сканер не запущен.
        """)
    void testCase3() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "LKOH", "SIBN", "TATN");
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-22T13:30:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1,1, 0);
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkoh().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T4. С последнего запуска прошел 1 час, сканер запущен.
        """)
    void testCase4() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "LKOH", "SIBN", "TATN");
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-22T14:00:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1,1, 0);
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkoh().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T5. Сканер создан для двух инструментов. TATN отстающий, ROSN растущий.
        Сигнала нет, ошибки нет.
        """)
    void testCase5() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "TATN");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T6. Сканер создан для трех инструментов. TATN падающий, ROSN растущий, SIBN растущий.
        Сигнала нет, ошибки нет.
        """)
    void testCase6() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "TATN", "SIBN");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    private void initScanner(UUID datasourceId, String... tickers) {
        commandBus().execute(
            CreateScannerCommand.builder()
                .workPeriodInMinutes(1)
                .description("Секторальный отстающий, нефтянка.")
                .datasourceId(datasourceId)
                .tickers(Arrays.asList(tickers))
                .properties(
                    SectoralRetardProperties.builder()
                        .historyScale(historyScale)
                        .intradayScale(intradayScale)
                        .build()
                )
                .build()
        );
    }

    private void initOilCompanyData(UUID datasourceId) {
        datasourceStorage()
            .initInstruments(
                List.of(
                    rosn(),
                    tatn(),
                    lkoh(),
                    sibn()
                )
            );
        commandBus().execute(new IntegrateInstrumentsCommand(datasourceId));
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, getTickers(datasourceId)));
    }

    private void initDealsTatnFallOtherRise(UUID datasourceId) {
        datasourceStorage().initDealDatas(
            List.of(
                buildContractBy(datasourceId, 1L, "BRF4", "10:00:00", 78D, 78000D, 1),
                buildContractBy(datasourceId,1L, "BRF4", "12:00:00", 96D, 96000D, 1),
                buildBuyDealBy(datasourceId,1L, "ROSN", "10:00:00", 250.1D, 136926D, 1),
                buildBuyDealBy(datasourceId,2L, "ROSN", "12:00:00", 255.1D, 136926D, 1),
                buildBuyDealBy(datasourceId,1L, "LKOH", "10:00:00", 248.1D, 136926D, 1),
                buildBuyDealBy(datasourceId,2L, "LKOH", "12:00:00", 255.1D, 136926D, 1),
                buildBuyDealBy(datasourceId,1L, "SIBN", "10:00:00", 248.1D, 136926D, 1),
                buildBuyDealBy(datasourceId,2L, "SIBN", "12:00:00", 255.1D, 136926D, 1),
                buildBuyDealBy(datasourceId,1L, "TATN", "10:00:00", 251.1D, 136926D, 1),
                buildBuyDealBy(datasourceId,2L, "TATN", "12:00:00", 247.1D, 136926D, 1),
                buildBuyDealBy(datasourceId,3L, "TATN", "13:45:00", 280.1D, 136926D, 1)
            )
        );
    }

    private void initTradingResultsForTestCase2(UUID datasourceId) {
        datasourceStorage().initTradingResults(
            List.of(
                //BRF4
                buildFuturesDealResultBy(datasourceId,"BRF4", "2023-12-20", 75D, 75D, 10D),
                buildFuturesDealResultBy(datasourceId,"BRF4", "2023-12-21", 80D, 80D, 10D),
                //ROSN
                buildDealResultBy(datasourceId,"ROSN", "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId,"ROSN", "2023-12-21", 250D, 255D, 1D, 1D),
                //LKOH
                buildDealResultBy(datasourceId,"LKOH", "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId,"LKOH", "2023-12-21", 250D, 255D, 1D, 1D),
                //SIBN
                buildDealResultBy(datasourceId,"SIBN", "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId,"SIBN", "2023-12-21", 250D, 255D, 1D, 1D),
                //TATN
                buildDealResultBy(datasourceId,"TATN", "2023-12-20", 250D, 252D, 1D, 1D),
                buildDealResultBy(datasourceId,"TATN", "2023-12-21", 250D, 253D, 1D, 1D)
            )
        );
    }

    private void initTradingResultsForTestCase1(UUID datasourceId) {
        datasourceStorage().initTradingResults(
            List.of(
                //BRF4
                buildFuturesDealResultBy(datasourceId,"BRF4", "2023-12-20", 75D, 75D, 10D),
                buildFuturesDealResultBy(datasourceId,"BRF4", "2023-12-21", 74D, 74D, 10D),
                //ROSN
                buildDealResultBy(datasourceId,"ROSN", "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId,"ROSN", "2023-12-21", 250D, 251D, 1D, 1D),
                //LKOH
                buildDealResultBy(datasourceId,"LKOH", "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId,"LKOH", "2023-12-21", 250D, 250D, 1D, 1D),
                //SIBN
                buildDealResultBy(datasourceId,"SIBN", "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId,"SIBN", "2023-12-21", 250D, 249D, 1D, 1D),
                //TATN
                buildDealResultBy(datasourceId,"TATN", "2023-12-20", 250D, 252D, 1D, 1D),
                buildDealResultBy(datasourceId,"TATN", "2023-12-21", 250D, 253D, 1D, 1D)
            )
        );
    }
}
