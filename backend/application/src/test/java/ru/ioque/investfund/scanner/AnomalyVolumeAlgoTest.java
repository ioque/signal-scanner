package ru.ioque.investfund.scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.algorithms.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        List<Signal> signals = fakeDataScannerStorage().getAll().get(0).getSignals();
        FinInstrument tgkn = fakeDataScannerStorage()
            .getAll()
            .get(0)
            .getFinInstruments()
            .stream()
            .filter(row -> row.getTicker().equals("TGKN"))
            .findFirst()
            .orElseThrow();
        FinInstrument tgkb = fakeDataScannerStorage()
            .getAll()
            .get(0)
            .getFinInstruments()
            .stream()
            .filter(row -> row.getTicker().equals("TGKB"))
            .findFirst()
            .orElseThrow();
        FinInstrument imoex = fakeDataScannerStorage()
            .getAll()
            .get(0)
            .getFinInstruments()
            .stream()
            .filter(row -> row.getTicker().equals("IMOEX"))
            .findFirst()
            .orElseThrow();
        assertEquals(2, signals.size());
        assertEquals(2, signals.stream().filter(Signal::isBuy).count());
        assertEquals(100.0, tgkn.getTodayOpenPrice().orElseThrow());
        assertEquals(102.0, tgkn.getTodayLastPrice().orElseThrow());
        assertEquals(13000.0, tgkn.getTodayValue().orElseThrow());
        assertEquals(1150.0, tgkn.getHistoryMedianValue().orElseThrow());
        assertTrue(tgkn.isRiseToday());

        assertEquals(100.0, tgkb.getTodayOpenPrice().orElseThrow());
        assertEquals(102.0, tgkb.getTodayLastPrice().orElseThrow());
        assertEquals(15000.0, tgkb.getTodayValue().orElseThrow());
        assertEquals(1500.0, tgkb.getHistoryMedianValue().orElseThrow());
        assertTrue(tgkb.isRiseToday());

        assertEquals(2800D, imoex.getTodayOpenPrice().orElseThrow());
        assertEquals(3100D, imoex.getTodayLastPrice().orElseThrow());
        assertEquals(2_200_000D, imoex.getTodayValue().orElseThrow());
        assertEquals(1_500_000D, imoex.getHistoryMedianValue().orElseThrow());
        assertTrue(imoex.isRiseToday());
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

    @Test
    @DisplayName("""
        T13. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутри дня сделок по бумаге не было.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase13() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
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
            buildDeltaBy(2L, "IMOEX", "12:00:00", 101D, 200D)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T14. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные не проинтегрированы, внутри дня сделок по бумаге не было.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase14() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTradingResults(
            buildDeltaResultBy("IMOEX", "2023-12-10", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-20", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-21", 100.D, 100.D, 1D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 98D, 100D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 101D, 200D)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T15. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутри дня была совершена одна сделка.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase15() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
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
            buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 469D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T16. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        Исторические данные по индексу есть, внутридневных данных по индексу нет.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase16() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTradingResults(
            buildDeltaResultBy("IMOEX", "2023-12-10", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-20", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-21", 100.D, 100.D, 1D)
        );
        initDealDatas(
            buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 46912035D, 1),
            buildBuyDealBy(2L, "TGKN", "10:03:00", 100D, 46912035D, 1),
            buildSellDealBy(3L, "TGKN", "11:00:00", 100D, 46912035D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T17. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Исторических данных по индексу нет, внутридневные данные по индексу есть.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase17() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTradingResults(
            buildDealResultBy("TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy("TGKN", "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 98D, 100D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 101D, 200D),
            buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 46912035D, 1),
            buildBuyDealBy(2L, "TGKN", "10:03:00", 100D, 46912035D, 1),
            buildSellDealBy(3L, "TGKN", "11:00:00", 100D, 46912035D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T18. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Исторических данных по индексу нет, внутридневные данные по индексу есть.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase18() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTradingResults(
            buildDealResultBy("TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy("TGKN", "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 98D, 100D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 101D, 200D)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T19. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы за один день, внутридневные данные проинтегрированы, объем превышает объем за предыдущий день.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase19() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTradingResults(
            buildDeltaResultBy("IMOEX", "2023-12-10", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-20", 99.D, 99.D, 1D),
            buildDeltaResultBy("IMOEX", "2023-12-21", 100.D, 100.D, 1D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 98D, 100D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 101D, 200D),
            buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 46912035D, 1),
            buildBuyDealBy(2L, "TGKN", "10:03:00", 100D, 46912035D, 1),
            buildSellDealBy(3L, "TGKN", "11:00:00", 100D, 46912035D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    @Test
    @DisplayName("""
        T20. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные проинтегрированы, объем превышает исторический.
        Исторические данные по индексу проинтегрированы за один день, дневные данные по индексу проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase20() {
        final var tickers = List.of("TGKN", "IMOEX");
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex();
        initTradingResults(
            buildDeltaResultBy("IMOEX", "2023-12-21", 100.D, 100.D, 1D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 98D, 100D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 101D, 200D),
            buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 46912035D, 1),
            buildBuyDealBy(2L, "TGKN", "10:03:00", 100D, 46912035D, 1),
            buildSellDealBy(3L, "TGKN", "11:00:00", 100D, 46912035D, 1)
        );
        exchangeManager().integrateWithDataSource();
        exchangeManager().enableUpdate(getInstrumentsBy(tickers).map(Instrument::getId).toList());
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentsBy(tickers).map(Instrument::getId).toList(), 1.5, 180, "IMOEX")
        );
        runWorkPipline();

        assertEquals(0, fakeDataScannerStorage().getAll().get(0).getSignals().size());
    }

    private void initTgknBuySignalDataset() {
        initTradingResults(
            buildDealResultBy("TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy("TGKN", "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D),
            buildDeltaResultBy("IMOEX", "2023-12-10", 2900D, 2900D, 1_000_000D),
            buildDeltaResultBy("IMOEX", "2023-12-20", 2900D, 2900D, 1_500_000D),
            buildDeltaResultBy("IMOEX", "2023-12-21", 3000D, 3000D, 2_000_000D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 2800D, 100D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 3200D, 200D),
            buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 5000D, 1),
            buildBuyDealBy(2L, "TGKN", "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(3L, "TGKN", "11:00:00", 100D, 1000D, 1),
            buildBuyDealBy(4L, "TGKN", "11:01:00", 100D, 1000D, 1),
            buildBuyDealBy(5L, "TGKN", "11:45:00", 102D, 5000D, 1)
        );
    }

    private void initTgknSellSignalDataset() {
        initTradingResults(
            buildDealResultBy("TGKN", "2023-12-22", 99.D, 99.1D, 97D, 1000D),
            buildDealResultBy("TGKN", "2023-12-23", 99.D, 99.1D, 97D, 1000D),
            buildDealResultBy("TGKN", "2023-12-24", 97.2D, 97.1D, 97D, 1000D),
            buildDeltaResultBy("IMOEX", "2023-12-22", 2900D, 2900D, 1_000_000D),
            buildDeltaResultBy("IMOEX", "2023-12-23", 2900D, 2900D, 1_500_000D),
            buildDeltaResultBy("IMOEX", "2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initDealDatas(
            buildDeltaBy(1L, "IMOEX", "10:00:00", 3000D, 1_000_000D),
            buildDeltaBy(2L, "IMOEX", "12:00:00", 2900D, 2_000_000D),
            buildBuyDealBy(1L, "TGKN", "10:00:00", 98D, 5000D, 1),
            buildSellDealBy(2L, "TGKN", "10:03:00", 97D, 1000D, 1),
            buildSellDealBy(3L, "TGKN", "11:00:00", 98D, 1000D, 1),
            buildSellDealBy(4L, "TGKN", "11:01:00", 97D, 1000D, 1),
            buildSellDealBy(5L, "TGKN", "11:45:00", 96D, 5000D, 1)
        );
    }

    private void initTgknAndTgkbAndImoexHistoryTradingData() {
        initTradingResults(
            buildDealResultBy("TGKB", "2023-12-19", 99.D, 99.D, 1D, 2000D),
            buildDealResultBy("TGKB", "2023-12-20", 99.D, 99.D, 1D, 1000D),
            buildDealResultBy("TGKB", "2023-12-21", 10.D, 10.D, 1D, 1500D),
            buildDealResultBy("TGKN", "2023-12-19", 99.D, 99.D, 1D, 3000D),
            buildDealResultBy("TGKN", "2023-12-20", 99.D, 99.D, 1D, 1150D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D, 1D, 1100D),
            buildDeltaResultBy("IMOEX", "2023-12-10", 2800D, 2900D, 1_000_000D),
            buildDeltaResultBy("IMOEX", "2023-12-20", 2800D, 2900D, 1_500_000D),
            buildDeltaResultBy("IMOEX", "2023-12-21", 2900D, 3000D, 2_000_000D)
        );
    }

    private void initTgknAndTgkbAndImoexIntradayData() {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDeltaBy(1L, "IMOEX", "10:00:00", 2800D, 1_000_000D),
                buildDeltaBy(2L, "IMOEX", "12:00:00", 3100D, 1_200_000D),
                //TGKB
                buildBuyDealBy(1L, "TGKB", "10:00:00", 100D, 6000D, 1),
                buildBuyDealBy(2L, "TGKB", "10:16:00", 100D, 1000D, 1),
                buildBuyDealBy(3L, "TGKB", "11:00:00", 100D, 1000D, 1),
                buildBuyDealBy(4L, "TGKB", "11:10:00", 100D, 1000D, 1),
                buildBuyDealBy(5L, "TGKB", "11:50:00", 102D, 6000D, 1),
                //TGKN
                buildBuyDealBy(1L, "TGKN", "10:00:00", 100D, 5000D, 1),
                buildBuyDealBy(2L, "TGKN", "10:03:00", 100D, 1000D, 1),
                buildBuyDealBy(3L, "TGKN", "11:00:00", 100D, 1000D, 1),
                buildBuyDealBy(4L, "TGKN", "11:01:00", 100D, 1000D, 1),
                buildBuyDealBy(5L, "TGKN", "11:45:00", 102D, 5000D, 1)
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
