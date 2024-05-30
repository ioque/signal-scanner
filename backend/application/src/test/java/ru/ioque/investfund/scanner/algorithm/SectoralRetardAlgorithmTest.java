package ru.ioque.investfund.scanner.algorithm;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.LKOH;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.ROSN;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.SIBN;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.TATN;

@DisplayName("SECTORAL RETARD ALGORITHM TEST")
public class SectoralRetardAlgorithmTest extends BaseAlgorithmTest {
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
        initTradingResultsForTestCase1();
        initDealsTatnFallOtherRise();
        initScanner(datasourceId, ROSN, LKOH, SIBN, TATN);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0,0, 0);
        assertFalse(getRosnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getSibnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getLkohPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
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
        initTradingResultsForTestCase2();
        initDealsTatnFallOtherRise();
        initScanner(datasourceId, ROSN, LKOH, SIBN, TATN);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 1,1, 0);
        assertTrue(getRosnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkohPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
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
        initTradingResultsForTestCase2();
        initDealsTatnFallOtherRise();
        initScanner(datasourceId, ROSN, TATN);

        runPipeline(datasourceId);

        assertSignals(getSignals(),  0,0, 0);
        assertTrue(getRosnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
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
        initTradingResultsForTestCase2();
        initDealsTatnFallOtherRise();
        initScanner(datasourceId, ROSN, TATN, SIBN);

        runPipeline(datasourceId);

        assertSignals(getSignals(), 0,0, 0);
        assertTrue(getSibnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getRosnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatnPerformance().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    private void initScanner(DatasourceId datasourceId, String... tickers) {
        commandBus().execute(
            CreateScanner.builder()
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
                    instrumentFixture.rosnDetails(),
                    instrumentFixture.tatnDetails(),
                    instrumentFixture.lkohDetails(),
                    instrumentFixture.sibn()
                )
            );
        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(new EnableUpdateInstruments(datasourceId, getTickers(datasourceId)));
    }

    private void initDealsTatnFallOtherRise() {
        datasourceStorage().initDealDatas(
            List.of(
                intradayFixture.brf4Contract(1L,"10:00:00", 78D, 78000D, 1),
                intradayFixture.brf4Contract(1L,"12:00:00", 96D, 96000D, 1),
                intradayFixture.rosnBuyDeal(1L,"10:00:00", 250.1D, 136926D, 1),
                intradayFixture.rosnBuyDeal(2L,"12:00:00", 255.1D, 136926D, 1),
                intradayFixture.lkohBuyDeal(1L,"10:00:00", 248.1D, 136926D, 1),
                intradayFixture.lkohBuyDeal(2L,"12:00:00", 255.1D, 136926D, 1),
                intradayFixture.sibnBuyDeal(1L,"10:00:00", 248.1D, 136926D, 1),
                intradayFixture.sibnBuyDeal(2L,"12:00:00", 255.1D, 136926D, 1),
                intradayFixture.tatnBuyDeal(1L,"10:00:00", 251.1D, 136926D, 1),
                intradayFixture.tatnBuyDeal(2L,"12:00:00", 247.1D, 136926D, 1),
                intradayFixture.tatnBuyDeal(3L,"13:45:00", 280.1D, 136926D, 1)
            )
        );
    }

    private void initTradingResultsForTestCase2() {
        datasourceStorage().initHistoryValues(
            List.of(
                //BRF4
                historyFixture.brf4HistoryValue("2023-12-20", 75D, 75D, 10D),
                historyFixture.brf4HistoryValue("2023-12-21", 80D, 80D, 10D),
                //ROSN
                historyFixture.rosnHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                historyFixture.rosnHistoryValue("2023-12-21", 250D, 255D, 1D, 1D),
                //LKOH
                historyFixture.lkohHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                historyFixture.lkohHistoryValue("2023-12-21", 250D, 255D, 1D, 1D),
                //SIBN
                historyFixture.sibnHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                historyFixture.sibnHistoryValue("2023-12-21", 250D, 255D, 1D, 1D),
                //TATN
                historyFixture.tatnHistoryValue("2023-12-20", 250D, 252D, 1D, 1D),
                historyFixture.tatnHistoryValue("2023-12-21", 250D, 253D, 1D, 1D)
            )
        );
    }

    private void initTradingResultsForTestCase1() {
        datasourceStorage().initHistoryValues(
            List.of(
                //BRF4
                historyFixture.brf4HistoryValue("2023-12-20", 75D, 75D, 10D),
                historyFixture.brf4HistoryValue("2023-12-21", 74D, 74D, 10D),
                //ROSN
                historyFixture.rosnHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                historyFixture.rosnHistoryValue("2023-12-21", 250D, 251D, 1D, 1D),
                //LKOH
                historyFixture.lkohHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                historyFixture.lkohHistoryValue("2023-12-21", 250D, 250D, 1D, 1D),
                //SIBN
                historyFixture.sibnHistoryValue("2023-12-20", 250D, 250D, 1D, 1D),
                historyFixture.sibnHistoryValue("2023-12-21", 250D, 249D, 1D, 1D),
                //TATN
                historyFixture.tatnHistoryValue("2023-12-20", 250D, 252D, 1D, 1D),
                historyFixture.tatnHistoryValue("2023-12-21", 250D, 253D, 1D, 1D)
            )
        );
    }
}
