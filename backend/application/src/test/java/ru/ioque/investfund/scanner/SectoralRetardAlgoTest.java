package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.algorithms.SectoralRetardSignalConfig;

import java.util.List;

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
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentIds(),null, intradayScale)
        ));
        assertEquals("Не передан параметр historyScale.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В конфигурацию SectoralRetardSignalConfig параметр historyScale передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр historyScale должен быть больше нуля."
        """)
    void testCase2() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentIds(),0D, intradayScale)
        ));
        assertEquals("Параметр historyScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В конфигурацию SectoralRetardSignalConfig параметр historyScale передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр historyScale должен быть больше нуля."
        """)
    void testCase3() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentIds(),-1D, intradayScale)
        ));
        assertEquals("Параметр historyScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В конфигурацию SectoralRetardSignalConfig не передан параметр intradayScale.
        Результат: ошибка, текст ошибки: "Не передан параметр intradayScale."
        """)
    void testCase4() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentIds(), historyScale, null)
        ));
        assertEquals("Не передан параметр intradayScale.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T5. В конфигурацию SectoralRetardSignalConfig параметр intradayScale передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр intradayScale должен быть больше нуля."
        """)
    void testCase5() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentIds(), historyScale, 0D)
        ));
        assertEquals("Параметр intradayScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T6. В конфигурацию SectoralRetardSignalConfig параметр intradayScale передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр intradayScale должен быть больше нуля."
        """)
    void testCase6() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentIds(), historyScale, -1D)
        ));
        assertEquals("Параметр intradayScale должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T7. ROSN, LKOH, SIBN, TATN - сектор нефти.
        3 из 4 позиций не росли в последние дни, сигнала нет.
        """)
    void testCase7() {
        final var tickers = List.of("ROSN", "LKOH", "SIBN", "TATN");
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData();
        initTradingResultsForTestCase1();
        initDealsTatnFallOtherRise();
        initScanner(tickers);
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());

        runWorkPipline();

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
        final var tickers = List.of("ROSN", "LKOH", "SIBN", "TATN");
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData();
        initTradingResultsForTestCase2();
        initDealsTatnFallOtherRise();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        initScanner(tickers);

        runWorkPipline();

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
        final var tickers = List.of("ROSN", "LKOH", "SIBN", "TATN");
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData();
        initTradingResultsForTestCase2();
        initDealsTatnFallOtherRise();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        initScanner(tickers);
        runWorkPipline();
        clearLogs();
        initTodayDateTime("2023-12-22T13:30:00");

        runWorkPipline();

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
        final var tickers = List.of("ROSN", "LKOH", "SIBN", "TATN");
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData();
        initTradingResultsForTestCase2();
        initDealsTatnFallOtherRise();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        initScanner(tickers);
        runWorkPipline();
        clearLogs();
        initTodayDateTime("2023-12-22T14:00:00");

        runWorkPipline();

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
        final var tickers = List.of("ROSN", "TATN");
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData();
        initTradingResultsForTestCase2();
        initDealsTatnFallOtherRise();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        initScanner(tickers);

        runWorkPipline();

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
        final var tickers = List.of("ROSN", "TATN", "SIBN");
        initTodayDateTime("2023-12-22T13:00:00");
        initOilCompanyData();
        initTradingResultsForTestCase2();
        initDealsTatnFallOtherRise();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        initScanner(tickers);

        runWorkPipline();

        assertSignals(getSignals(), 0, 0, 0);
        assertTrue(getSibn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertTrue(getRosn().isRiseInLastTwoDay(historyScale, intradayScale));
        assertFalse(getTatn().isRiseInLastTwoDay(historyScale, intradayScale));
    }

    private void initScanner(List<String> tickers) {
        addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), historyScale, intradayScale)
        );
    }

    private void initOilCompanyData() {
        exchangeDataFixture()
            .initInstruments(
                List.of(
                    imoex(),
                    usdRub(),
                    rosn(),
                    tatn(),
                    lkoh(),
                    sibn(),
                    brf4()
                )
            );
        exchangeManager().integrateWithDataSource();
    }

    private void initDealsTatnFallOtherRise() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildFuturesDealBy(1L, "BRF4",  "10:00:00",78D, 78000D,1),
                buildFuturesDealBy(1L, "BRF4", "12:00:00", 96D, 96000D,1),
                buildBuyDealBy(1L, "ROSN", "10:00:00", 250.1D,136926D, 1),
                buildBuyDealBy(2L, "ROSN", "12:00:00", 255.1D,136926D, 1),
                buildBuyDealBy(1L, "LKOH", "10:00:00", 248.1D,136926D, 1),
                buildBuyDealBy(2L, "LKOH", "12:00:00", 255.1D,136926D, 1),
                buildBuyDealBy(1L, "SIBN", "10:00:00", 248.1D,136926D, 1),
                buildBuyDealBy(2L, "SIBN", "12:00:00", 255.1D,136926D, 1),
                buildBuyDealBy(1L, "TATN", "10:00:00", 251.1D,136926D, 1),
                buildBuyDealBy(2L, "TATN", "12:00:00", 247.1D,136926D, 1),
                buildBuyDealBy(3L, "TATN", "13:45:00", 280.1D,136926D, 1)
            )
        );
    }

    private void initTradingResultsForTestCase2() {
        exchangeDataFixture().initTradingResults(
            List.of(
                //BRF4
                buildFuturesDealResultBy("BRF4", "2023-12-20", 75D, 75D, 10D, 1),
                buildFuturesDealResultBy("BRF4", "2023-12-21", 80D, 80D, 10D, 1),
                //ROSN
                buildDealResultBy("ROSN", "2023-12-20",  250D,250D,1D, 1D),
                buildDealResultBy("ROSN", "2023-12-21",  250D,255D,1D, 1D),
                //LKOH
                buildDealResultBy("LKOH", "2023-12-20",  250D,250D, 1D,1D),
                buildDealResultBy("LKOH", "2023-12-21",  250D,255D,1D, 1D),
                //SIBN
                buildDealResultBy("SIBN", "2023-12-20",  250D,250D, 1D,1D),
                buildDealResultBy("SIBN", "2023-12-21",  250D,255D,1D, 1D),
                //TATN
                buildDealResultBy("TATN", "2023-12-20",  250D,252D, 1D,1D),
                buildDealResultBy("TATN", "2023-12-21",  250D,253D,1D, 1D)
            )
        );
    }

    private void initTradingResultsForTestCase1() {
        exchangeDataFixture().initTradingResults(
            List.of(
                //BRF4
                buildFuturesDealResultBy("BRF4", "2023-12-20", 75D, 75D, 10D, 1),
                buildFuturesDealResultBy("BRF4", "2023-12-21", 74D, 74D, 10D, 1),
                //ROSN
                buildDealResultBy("ROSN", "2023-12-20", 250D, 250D, 1D,1D),
                buildDealResultBy("ROSN", "2023-12-21", 250D, 251D, 1D,1D),
                //LKOH
                buildDealResultBy("LKOH", "2023-12-20",  250D,250D, 1D,1D),
                buildDealResultBy("LKOH", "2023-12-21", 250D, 250D, 1D,1D),
                //SIBN
                buildDealResultBy("SIBN", "2023-12-20", 250D, 250D, 1D,1D),
                buildDealResultBy("SIBN", "2023-12-21", 250D, 249D, 1D,1D),
                //TATN
                buildDealResultBy("TATN", "2023-12-20", 250D, 252D, 1D,1D),
                buildDealResultBy("TATN", "2023-12-21", 250D, 253D, 1D,1D)
            )
        );
    }
}
