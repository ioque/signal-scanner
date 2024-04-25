package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;
import ru.ioque.investfund.application.scanner.command.CreateScannerCommand;

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
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase1(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId, ROSN, LKOH, SIBN, TATN);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertFalse(getRosnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getSibnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getLkohSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T2. ROSN, LKOH, SIBN росли в предыдущий день, растут сегодня.
        TATN росла вчера, сегодня падает.
        """)
    void testCase2() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId, ROSN, LKOH, SIBN, TATN);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1,1, 0);
        assertTrue(getRosnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkohSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T3. С последнего запуска прошло меньше 1 часа, сканер не запущен.
        """)
    void testCase3() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId, ROSN, LKOH, SIBN, TATN);
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-22T13:30:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1,1, 0);
        assertTrue(getRosnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkohSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T4. С последнего запуска прошел 1 час, сканер запущен.
        """)
    void testCase4() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId, ROSN, LKOH, SIBN, TATN);
        runWorkPipelineAndClearLogs(datasourceId);
        initTodayDateTime("2023-12-22T14:00:00");

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 1, 1,1, 0);
        assertTrue(getRosnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkohSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T5. Сканер создан для двух инструментов. TATN отстающий, ROSN растущий.
        Сигнала нет, ошибки нет.
        """)
    void testCase5() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId, ROSN, TATN);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertTrue(getRosnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T6. Сканер создан для трех инструментов. TATN падающий, ROSN растущий, SIBN растущий.
        Сигнала нет, ошибки нет.
        """)
    void testCase6() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId, ROSN, TATN, SIBN);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertTrue(getSibnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getRosnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnSnapshot().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    private void initScanner(DatasourceId datasourceId, String... tickers) {
        commandBus().execute(
            CreateScannerCommand.builder()
                .track(UUID.randomUUID())
                .workPeriodInMinutes(1)
                .description("Секторальный отстающий, нефтянка.")
                .datasourceId(datasourceId)
                .tickers(Arrays.stream(tickers).map(Ticker::from).toList())
                .properties(
                    SectoralRetardProperties.builder()
                        .historyScale(historyScale)
                        .intradayScale(intradayScale)
                        .build()
                )
                .build()
        );
    }

    private void initOilCompanyData(DatasourceId datasourceId) {
        datasourceStorage()
            .initInstrumentDetails(
                List.of(
                    rosnDetails(),
                    tatnDetails(),
                    lkohDetails(),
                    sibn()
                )
            );
        commandBus().execute(new IntegrateInstrumentsCommand(UUID.randomUUID(), datasourceId));
        commandBus().execute(new EnableUpdateInstrumentsCommand(UUID.randomUUID(), datasourceId, getTickers(datasourceId)));
    }

    private void initDealsTatnFallOtherRise(DatasourceId datasourceId) {
        datasourceStorage().initDealDatas(
            List.of(
                buildBrf4Contract(1L,"10:00:00", 78D, 78000D, 1),
                buildBrf4Contract(1L,"12:00:00", 96D, 96000D, 1),
                buildRosnBuyDeal(1L,"10:00:00", 250.1D, 136926D, 1),
                buildRosnBuyDeal(2L,"12:00:00", 255.1D, 136926D, 1),
                buildLkohBuyDeal(1L,"10:00:00", 248.1D, 136926D, 1),
                buildLkohBuyDeal(2L,"12:00:00", 255.1D, 136926D, 1),
                buildSibnBuyDeal(1L,"10:00:00", 248.1D, 136926D, 1),
                buildSibnBuyDeal(2L,"12:00:00", 255.1D, 136926D, 1),
                buildTatnBuyDeal(1L,"10:00:00", 251.1D, 136926D, 1),
                buildTatnBuyDeal(2L,"12:00:00", 247.1D, 136926D, 1),
                buildTatnBuyDeal(3L,"13:45:00", 280.1D, 136926D, 1)
            )
        );
    }

    private void initTradingResultsForTestCase2(DatasourceId datasourceId) {
        datasourceStorage().initHistoryValues(
            List.of(
                //BRF4
                buildBrf4HistoryValue("2023-12-20", 75D, 75D, 10D),
                buildBrf4HistoryValue("2023-12-21", 80D, 80D, 10D),
                //ROSN
                buildRosnHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                buildRosnHistoryValue("2023-12-21", 250D, 255D, 1D, 1D),
                //LKOH
                buildLkohHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                buildLkohHistoryValue("2023-12-21", 250D, 255D, 1D, 1D),
                //SIBN
                buildSibnHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                buildSibnHistoryValue("2023-12-21", 250D, 255D, 1D, 1D),
                //TATN
                buildTatnHistoryValue("2023-12-20", 250D, 252D, 1D, 1D),
                buildTatnHistoryValue("2023-12-21", 250D, 253D, 1D, 1D)
            )
        );
    }

    private void initTradingResultsForTestCase1(DatasourceId datasourceId) {
        datasourceStorage().initHistoryValues(
            List.of(
                //BRF4
                buildBrf4HistoryValue("2023-12-20", 75D, 75D, 10D),
                buildBrf4HistoryValue("2023-12-21", 74D, 74D, 10D),
                //ROSN
                buildRosnHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                buildRosnHistoryValue("2023-12-21", 250D, 251D, 1D, 1D),
                //LKOH
                buildLkohHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                buildLkohHistoryValue("2023-12-21", 250D, 250D, 1D, 1D),
                //SIBN
                buildSibnHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                buildSibnHistoryValue("2023-12-21", 250D, 249D, 1D, 1D),
                //TATN
                buildTatnHistoryValue("2023-12-20", 250D, 252D, 1D, 1D),
                buildTatnHistoryValue("2023-12-21", 250D, 253D, 1D, 1D)
            )
        );
    }
}
