package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.algorithms.SectoralRetardSignalConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SIGNAL SCANNER MANAGER - SECTORAL RETARD ALGORITHM")
public class SectoralRetardAlgoTest extends BaseScannerTest {
    @Test
    @DisplayName("""
        T1. В конфигурацию SectoralRetardSignalConfig не передан параметр historyScale.
        Результат: ошибка, текст ошибки: "Не передан параметр historyScale."
        """)
    void testCase1() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentIds(),null, 1D)
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
            new SectoralRetardSignalConfig(getInstrumentIds(),0D, 1D)
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
            new SectoralRetardSignalConfig(getInstrumentIds(),-1D, 1D)
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
            new SectoralRetardSignalConfig(getInstrumentIds(),1D, null)
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
            new SectoralRetardSignalConfig(getInstrumentIds(),1D, 0D)
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
            new SectoralRetardSignalConfig(getInstrumentIds(),1D, -1D)
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
        addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(),0.015, 0.015)
        );
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        runWorkPipline();

        var instruments = getInstruments();
        var rosn = instruments.stream().filter(row -> row.getTicker().equals("ROSN")).findFirst().orElseThrow();
        var lkoh = instruments.stream().filter(row -> row.getTicker().equals("LKOH")).findFirst().orElseThrow();
        var sibn = instruments.stream().filter(row -> row.getTicker().equals("SIBN")).findFirst().orElseThrow();
        var tatn = instruments.stream().filter(row -> row.getTicker().equals("TATN")).findFirst().orElseThrow();

        assertEquals(2, rosn.getDailyValues().size());
        assertEquals(2, lkoh.getDailyValues().size());
        assertEquals(2, sibn.getDailyValues().size());
        assertEquals(2, tatn.getDailyValues().size());
        assertEquals(2, rosn.getIntradayValues().size());
        assertEquals(2, lkoh.getIntradayValues().size());
        assertEquals(2, sibn.getIntradayValues().size());
        assertEquals(3, tatn.getIntradayValues().size());
        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
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
        addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 0.015, 0.015)
        );

        runWorkPipline();

        var instruments = getInstruments();
        var rosn = instruments.stream().filter(row -> row.getTicker().equals("ROSN")).findFirst().orElseThrow();
        var lkoh = instruments.stream().filter(row -> row.getTicker().equals("LKOH")).findFirst().orElseThrow();
        var sibn = instruments.stream().filter(row -> row.getTicker().equals("SIBN")).findFirst().orElseThrow();
        var tatn = instruments.stream().filter(row -> row.getTicker().equals("TATN")).findFirst().orElseThrow();

        assertEquals(2, rosn.getDailyValues().size());
        assertEquals(2, lkoh.getDailyValues().size());
        assertEquals(2, sibn.getDailyValues().size());
        assertEquals(2, tatn.getDailyValues().size());
        assertEquals(2, rosn.getIntradayValues().size());
        assertEquals(2, lkoh.getIntradayValues().size());
        assertEquals(2, sibn.getIntradayValues().size());
        assertEquals(3, tatn.getIntradayValues().size());
        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
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
        addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 0.015, 0.015)
        );
        runWorkPipline();
        clearLogs();
        initTodayDateTime("2023-12-22T13:30:00");

        runWorkPipline();
        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
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
        addScanner(
            "Секторальный отстающий, нефтянка.",
            new SectoralRetardSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 0.015, 0.015)
        );
        runWorkPipline();
        clearLogs();
        initTodayDateTime("2023-12-22T14:00:00");

        runWorkPipline();
        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
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
