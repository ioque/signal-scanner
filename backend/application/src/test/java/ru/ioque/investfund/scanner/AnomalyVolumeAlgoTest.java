package ru.ioque.investfund.scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.algorithms.AnomalyVolumeSignalConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SIGNAL SCANNER MANAGER - ANOMALY VOLUME ALGORITHM")
public class AnomalyVolumeAlgoTest extends BaseScannerTest {
    @Test
    @DisplayName("""
        T1. В конфигурацию AnomalyVolumeSignalConfig не передан параметр scaleCoefficient.
        Результат: ошибка, текст ошибки: "Не передан параметр scaleCoefficient."
        """)
    void testCase1() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), null, 180, "IMOEX")
        ));
        assertEquals("Не передан параметр scaleCoefficient.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В конфигурацию AnomalyVolumeSignalConfig не передан параметр historyPeriod.
        Результат: ошибка, текст ошибки: "Не передан параметр historyPeriod."
        """)
    void testCase2() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, null, "IMOEX")
        ));
        assertEquals("Не передан параметр historyPeriod.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В конфигурацию AnomalyVolumeSignalConfig не передан параметр indexTicker.
        Результат: ошибка, текст ошибки: "Не передан параметр indexTicker."
        """)
    void testCase3() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, 180, null)
        ));
        assertEquals("Не передан параметр indexTicker.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В конфигурацию AnomalyVolumeSignalConfig параметр indexTicker передан как пустая строка.
        Результат: ошибка, текст ошибки: "Не передан параметр indexTicker."
        """)
    void testCase4() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, 180, "")
        ));
        assertEquals("Не передан параметр indexTicker.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T5. В конфигурацию AnomalyVolumeSignalConfig параметр scaleCoefficient передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр scaleCoefficient должен быть больше нуля."
        """)
    void testCase5() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 0D, 180, "IMOEX")
        ));
        assertEquals("Параметр scaleCoefficient должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T6. В конфигурацию AnomalyVolumeSignalConfig параметр scaleCoefficient передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр scaleCoefficient должен быть больше нуля."
        """)
    void testCase6() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), -1D, 180, "IMOEX")
        ));
        assertEquals("Параметр scaleCoefficient должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T7. В конфигурацию AnomalyVolumeSignalConfig параметр historyPeriod передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр historyPeriod должен быть больше нуля."
        """)
    void testCase7() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, 0, "IMOEX")
        ));
        assertEquals("Параметр historyPeriod должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T8. В конфигурацию AnomalyVolumeSignalConfig параметр historyPeriod передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр historyPeriod должен быть больше нуля."
        """)
    void testCase8() {
        var error = assertThrows(DomainException.class, () -> addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, -180, "IMOEX")
        ));
        assertEquals("Параметр historyPeriod должен быть больше нуля.", error.getMessage());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        Т9. Создан сканер сигналов AnomalyVolumeScannerSignal для двух инструментов.
        По обоим инструментам есть торговые данные. По обоим инструментам объемы превышают средневзвешенные больше, чем
        в scaleCoefficient-раз.
        Результат: зарегистрировано два сигнала.
        """)
    void testCase9() {
        final var tickers = List.of("TGKN", "TGKB", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTgknAndTgkbAndImoexHistoryTradingData();
        initTgknAndTgkbAndImoexIntradayData();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();
        var instruments = getInstruments();
        var imoex = instruments.stream().filter(row -> row.getTicker().equals("IMOEX")).findFirst().orElseThrow();
        var tgkn = instruments.stream().filter(row -> row.getTicker().equals("TGKN")).findFirst().orElseThrow();
        var tgkb = instruments.stream().filter(row -> row.getTicker().equals("TGKB")).findFirst().orElseThrow();
        assertEquals(3, imoex.getDailyValues().size());
        assertEquals(3, tgkn.getDailyValues().size());
        assertEquals(3, tgkb.getDailyValues().size());
        assertEquals(2, imoex.getIntradayValues().size());
        assertEquals(5, tgkn.getIntradayValues().size());
        assertEquals(5, tgkb.getIntradayValues().size());
        assertEquals(2, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T10. С последнего запуска прошло меньше минуты, сканер не запущен.
        """)
    void testCase10() {
        final var tickers = List.of("TGKN", "TGKB", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTgknAndTgkbAndImoexHistoryTradingData();
        initTgknAndTgkbAndImoexIntradayData();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();
        loggerProvider().clearLogs();
        initTodayDateTime("2023-12-22T13:00:30");

        runWorkPipline();

        assertEquals(8, loggerProvider().log.size());
        assertEquals(2, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T11. С последнего запуска прошла минута, сканер запущен.
        """)
    void testCase11() {
        final var tickers = List.of("TGKN", "TGKB", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTgknAndTgkbAndImoexHistoryTradingData();
        initTgknAndTgkbAndImoexIntradayData();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        exchangeManager().execute();
        dataScannerManager().execute();
        loggerProvider().clearLogs();
        initTodayDateTime("2023-12-22T13:01:00");

        runWorkPipline();

        assertEquals(10, loggerProvider().log.size());
        assertEquals(2, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T12. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Ранее был зарегистрирован сигнал к покупке. В текущем массиве данных
        объем торгов провышает медиану в scaleCoefficient-раз. Объем продаж превышает объем покупок.
        Результат: зарегистрирован сигнал к продаже. Сигнал к покупке закрыт.
        """)
    void testCase12() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTgknBuySignalDataset();
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();
        loggerProvider().clearLogs();
        getInstrumentsBy(tickers).forEach(row -> row.getIntradayValues().clear());
        initTodayDateTime("2023-12-24T12:00:00");
        initTgknSellSignalDataset();

        runWorkPipline();

        assertEquals(1, fakeDataScannerStorage().getAll().get(0).getSignals().size());
        assertFalse(fakeDataScannerStorage().getAll().get(0).getSignals().get(0).isBuy());
    }

    private void initTgknBuySignalDataset() {
        initTradingResults(
            buildDealResultBy("TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy("TGKN", "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D),
            buildDeltaResultBy("IMOEX", "2023-12-10", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-20", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-21", 100.D, 100.D, 1D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 98D, 100D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 101D, 200D),
            buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 46912035D, 1),
            buildBuyDealBy(2L, "TGKN", "10:03:00", 100D, 46912035D, 1),
            buildSellDealBy(3L, "TGKN", "11:00:00", 100D, 46912035D, 1),
            buildBuyDealBy(4L, "TGKN", "11:01:00", 100D, 46912035D, 1),
            buildBuyDealBy(5L, "TGKN", "11:45:00", 102D, 46912035D, 1)
        );
    }

    private void initTgknSellSignalDataset() {
        initTradingResults(
            buildDealResultBy("TGKN", "2023-12-22", 99.D, 99.1D, 97D, 1000D),
            buildDealResultBy("TGKN", "2023-12-23", 99.D, 99.1D, 97D, 1000D),
            buildDealResultBy("TGKN", "2023-12-24", 97.2D, 97.1D, 97D, 1000D),
            buildDeltaResultBy("IMOEX", "2023-12-22", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-23", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-24", 100.D, 100.D, 1D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 98D, 100D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 97D, 200D),
            buildBuyDealBy(1L, "TGKN", "10:00:00", 98D, 46912035D, 1),
            buildSellDealBy(2L, "TGKN", "10:03:00", 97D, 46912035D, 1),
            buildSellDealBy(3L, "TGKN", "11:00:00", 98D, 46912035D, 1),
            buildSellDealBy(4L, "TGKN", "11:01:00", 97D, 46912035D, 1),
            buildSellDealBy(5L, "TGKN", "11:45:00", 96D, 46912035D, 1)
        );
    }

    private void initTgknAndTgkbAndImoexHistoryTradingData() {
        initTradingResults(
            buildDealResultBy("TGKB", "2023-12-19", 99.D, 99.D, 1D, 1000D),
            buildDealResultBy("TGKB", "2023-12-20", 99.D, 99.D, 1D, 1000D),
            buildDealResultBy("TGKB", "2023-12-21", 10.D, 10.D, 1D, 1000D),
            buildDealResultBy("TGKN", "2023-12-19", 99.D, 99.D, 1D, 1000D),
            buildDealResultBy("TGKN", "2023-12-20", 99.D, 99.D, 1D, 1000D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D, 1D, 1000D),
            buildDeltaResultBy("IMOEX", "2023-12-10", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-20", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-21", 100.D, 100.D, 1D)
        );
    }

    private void initTgknAndTgkbAndImoexIntradayData() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDeltaBy(1L, "IMOEX", "10:00:00", 98D, 100D),
                buildDeltaBy(2L, "IMOEX", "12:00:00", 101D, 200D),
                //TGKB
                buildBuyDealBy(1L, "TGKB", "10:00:00", 100D, 136926D, 1),
                buildBuyDealBy(2L, "TGKB", "10:16:00", 100D, 8736926D, 1),
                buildBuyDealBy(3L, "TGKB", "11:00:00", 100D, 8736926D, 1),
                buildBuyDealBy(4L, "TGKB", "11:10:00", 100D, 8736926D, 1),
                buildBuyDealBy(5L, "TGKB", "11:50:00", 102D, 873160926D, 1),
                //TGKN
                buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 46912035D, 1),
                buildBuyDealBy(2L, "TGKN", "10:03:00", 100D, 46912035D, 1),
                buildBuyDealBy(3L, "TGKN", "11:00:00", 100D, 46912035D, 1),
                buildBuyDealBy(4L, "TGKN", "11:01:00", 100D, 46912035D, 1),
                buildBuyDealBy(5L, "TGKN", "11:45:00", 102D, 46912035D, 1)
            )
        );
    }

    private void initTgknAndTgkbAndImoex() {
        exchangeDataFixture().initInstruments(
            List.of(
                imoex(),
                brf4(),
                usdRub(),
                tgkb(),
                tgkn()
            )
        );
    }
}
