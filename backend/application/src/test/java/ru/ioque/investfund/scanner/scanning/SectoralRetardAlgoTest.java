package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;

import java.util.Arrays;
import java.util.List;

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
        initScanner(datasourceId,rosnId, lkohId, sibnId, tatnId);

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
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,rosnId, lkohId, sibnId, tatnId);

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
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,rosnId, lkohId, sibnId, tatnId);
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
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,rosnId, lkohId, sibnId, tatnId);
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
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,rosnId, tatnId);

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
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,rosnId, tatnId, sibnId);

        runWorkPipeline(datasourceId);

        assertSignals(getSignals(), 0, 0,0, 0);
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    private void initScanner(DatasourceId datasourceId, InstrumentId... instrumentIds) {
        commandBus().execute(
            CreateScannerCommand.builder()
                .workPeriodInMinutes(1)
                .description("Секторальный отстающий, нефтянка.")
                .datasourceId(datasourceId)
                .instrumentIds(Arrays.asList(instrumentIds))
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
            .initInstruments(
                List.of(
                    rosn(),
                    tatn(),
                    lkoh(),
                    sibn()
                )
            );
        commandBus().execute(new IntegrateInstrumentsCommand(datasourceId));
        commandBus().execute(new EnableUpdateInstrumentsCommand(datasourceId, getInstrumentIds(datasourceId)));
    }

    private void initDealsTatnFallOtherRise(DatasourceId datasourceId) {
        datasourceStorage().initDealDatas(
            List.of(
                buildContractBy(datasourceId, brf4Id, 1L,"10:00:00", 78D, 78000D, 1),
                buildContractBy(datasourceId, brf4Id,1L,"12:00:00", 96D, 96000D, 1),
                buildBuyDealBy(datasourceId, rosnId,1L,"10:00:00", 250.1D, 136926D, 1),
                buildBuyDealBy(datasourceId, rosnId,2L,"12:00:00", 255.1D, 136926D, 1),
                buildBuyDealBy(datasourceId, lkohId,1L,"10:00:00", 248.1D, 136926D, 1),
                buildBuyDealBy(datasourceId, lkohId,2L,"12:00:00", 255.1D, 136926D, 1),
                buildBuyDealBy(datasourceId, sibnId,1L,"10:00:00", 248.1D, 136926D, 1),
                buildBuyDealBy(datasourceId, sibnId,2L,"12:00:00", 255.1D, 136926D, 1),
                buildBuyDealBy(datasourceId, tatnId,1L,"10:00:00", 251.1D, 136926D, 1),
                buildBuyDealBy(datasourceId, tatnId,2L,"12:00:00", 247.1D, 136926D, 1),
                buildBuyDealBy(datasourceId, tatnId,3L,"13:45:00", 280.1D, 136926D, 1)
            )
        );
    }

    private void initTradingResultsForTestCase2(DatasourceId datasourceId) {
        datasourceStorage().initTradingResults(
            List.of(
                //BRF4
                buildFuturesDealResultBy(datasourceId, brf4Id, "2023-12-20", 75D, 75D, 10D),
                buildFuturesDealResultBy(datasourceId, brf4Id, "2023-12-21", 80D, 80D, 10D),
                //ROSN
                buildDealResultBy(datasourceId, rosnId, "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId, rosnId, "2023-12-21", 250D, 255D, 1D, 1D),
                //LKOH
                buildDealResultBy(datasourceId, lkohId, "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId, lkohId, "2023-12-21", 250D, 255D, 1D, 1D),
                //SIBN
                buildDealResultBy(datasourceId, sibnId, "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId, sibnId, "2023-12-21", 250D, 255D, 1D, 1D),
                //TATN
                buildDealResultBy(datasourceId, tatnId, "2023-12-20", 250D, 252D, 1D, 1D),
                buildDealResultBy(datasourceId, tatnId, "2023-12-21", 250D, 253D, 1D, 1D)
            )
        );
    }

    private void initTradingResultsForTestCase1(DatasourceId datasourceId) {
        datasourceStorage().initTradingResults(
            List.of(
                //BRF4
                buildFuturesDealResultBy(datasourceId, brf4Id, "2023-12-20", 75D, 75D, 10D),
                buildFuturesDealResultBy(datasourceId, brf4Id, "2023-12-21", 74D, 74D, 10D),
                //ROSN
                buildDealResultBy(datasourceId, rosnId, "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId, rosnId, "2023-12-21", 250D, 251D, 1D, 1D),
                //LKOH
                buildDealResultBy(datasourceId, lkohId, "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId, lkohId, "2023-12-21", 250D, 250D, 1D, 1D),
                //SIBN
                buildDealResultBy(datasourceId, sibnId, "2023-12-20", 250D, 250D, 1D, 1D),
                buildDealResultBy(datasourceId, sibnId, "2023-12-21", 250D, 249D, 1D, 1D),
                //TATN
                buildDealResultBy(datasourceId, tatnId, "2023-12-20", 250D, 252D, 1D, 1D),
                buildDealResultBy(datasourceId, tatnId, "2023-12-21", 250D, 253D, 1D, 1D)
            )
        );
    }
}
