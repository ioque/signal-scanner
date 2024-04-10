package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.configurator.entity.SectoralRetardAlgorithmConfig;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SIGNAL SCANNER MANAGER - SECTORAL RETARD ALGORITHM")
public class SectoralRetardAlgoTest extends BaseScannerTest {
    private final Double historyScale = 0.015;
    private final Double intradayScale = 0.015;

    @Test
    @DisplayName("""
        T1. В конфигурацию SectoralRetardSignalConfig не передан параметр historyScale.
        Результат: ошибка, текст ошибки: "Не передан параметр historyScale."
        """)
    void testCase1() {
        final UUID datasourceId = getDatasourceId();
        initOilCompanyData(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Секторальный отстающий, нефтянка.",
            datasourceId,
            getTickers(datasourceId),
            new SectoralRetardAlgorithmConfig(
                null,
                intradayScale
            )
        ));

        assertEquals("Не передан параметр historyScale.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В конфигурацию SectoralRetardSignalConfig параметр historyScale передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр historyScale должен быть больше нуля."
        """)
    void testCase2() {
        final UUID datasourceId = getDatasourceId();
        initOilCompanyData(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Секторальный отстающий, нефтянка.",
            datasourceId,
            getTickers(datasourceId),
            new SectoralRetardAlgorithmConfig(
                0D,
                intradayScale
            )
        ));

        assertEquals("Параметр historyScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В конфигурацию SectoralRetardSignalConfig параметр historyScale передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр historyScale должен быть больше нуля."
        """)
    void testCase3() {
        final UUID datasourceId = getDatasourceId();
        initOilCompanyData(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Секторальный отстающий, нефтянка.",
            datasourceId,
            getTickers(datasourceId),
            new SectoralRetardAlgorithmConfig(
                -1D,
                intradayScale
            )
        ));

        assertEquals("Параметр historyScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В конфигурацию SectoralRetardSignalConfig не передан параметр intradayScale.
        Результат: ошибка, текст ошибки: "Не передан параметр intradayScale."
        """)
    void testCase4() {
        final UUID datasourceId = getDatasourceId();
        initOilCompanyData(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Секторальный отстающий, нефтянка.",
            datasourceId,
            getTickers(datasourceId),
            new SectoralRetardAlgorithmConfig(
                historyScale, null
            )
        ));

        assertEquals("Не передан параметр intradayScale.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T5. В конфигурацию SectoralRetardSignalConfig параметр intradayScale передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр intradayScale должен быть больше нуля."
        """)
    void testCase5() {
        final UUID datasourceId = getDatasourceId();
        initOilCompanyData(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Секторальный отстающий, нефтянка.",
            datasourceId,
            getTickers(datasourceId),
            new SectoralRetardAlgorithmConfig(
                historyScale,
                0D
            )
        ));

        assertEquals("Параметр intradayScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T6. В конфигурацию SectoralRetardSignalConfig параметр intradayScale передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр intradayScale должен быть больше нуля."
        """)
    void testCase6() {
        final UUID datasourceId = getDatasourceId();
        initOilCompanyData(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Секторальный отстающий, нефтянка.",
            datasourceId,
            getTickers(datasourceId),
            new SectoralRetardAlgorithmConfig(
                historyScale,
                -1D
            )
        ));

        assertEquals("Параметр intradayScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T7. ROSN, LKOH, SIBN, TATN - сектор нефти.
        3 из 4 позиций не росли в последние дни, сигнала нет.
        """)
    void testCase7() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase1(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "LKOH", "SIBN", "TATN");

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFalse(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getLkoh().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T8. ROSN, LKOH, SIBN росли в предыдущий день, растут сегодня.
        TATN росла вчера, сегодня падает.
        """)
    void testCase8() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "LKOH", "SIBN", "TATN");

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkoh().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T9. С последнего запуска прошло меньше 1 часа, сканер не запущен.
        """)
    void testCase9() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "LKOH", "SIBN", "TATN");
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-22T13:30:00");

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkoh().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T10. С последнего запуска прошел 1 час, сканер запущен.
        """)
    void testCase10() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "LKOH", "SIBN", "TATN");
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-22T14:00:00");

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getLkoh().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T11. Сканер создан для двух инструментов. TATN отстающий, ROSN растущий.
        Сигнала нет, ошибки нет.
        """)
    void testCase11() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "TATN");

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    @Test
    @DisplayName("""
        T12. Сканер создан для трех инструментов. TATN падающий, ROSN растущий, SIBN растущий.
        Сигнала нет, ошибки нет.
        """)
    void testCase12() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData(datasourceId);
        initTradingResultsForTestCase2(datasourceId);
        initDealsTatnFallOtherRise(datasourceId);
        initScanner(datasourceId,"ROSN", "TATN", "SIBN");

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    private void initScanner(UUID datasourceId, String... tickers) {
        addScanner(
            1,
            "Секторальный отстающий, нефтянка.",
            datasourceId,
            getInstrumentsBy(datasourceId, Arrays.asList(tickers)).map(Instrument::getTicker).toList(),
            new SectoralRetardAlgorithmConfig(
                historyScale,
                intradayScale
            )
        );
    }

    private void initOilCompanyData(UUID datasourceId) {
        exchangeDataFixture()
            .initInstruments(
                List.of(
                    rosn(),
                    tatn(),
                    lkoh(),
                    sibn()
                )
            );
        exchangeManager().integrateInstruments(datasourceId);
        exchangeManager().enableUpdate(datasourceId, getTickers(datasourceId));
    }



    private void initDealsTatnFallOtherRise(UUID datasourceId) {
        exchangeDataFixture().initDealDatas(
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
        exchangeDataFixture().initTradingResults(
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
        exchangeDataFixture().initTradingResults(
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
