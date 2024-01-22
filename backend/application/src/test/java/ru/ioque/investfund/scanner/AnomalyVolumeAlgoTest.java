package ru.ioque.investfund.scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.scanner.financial.algorithms.AnomalyVolumeSignalConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            new AnomalyVolumeSignalConfig(null, 180, "IMOEX"),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new AnomalyVolumeSignalConfig(1.5, null, "IMOEX"),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new AnomalyVolumeSignalConfig(1.5, 180, null),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new AnomalyVolumeSignalConfig(1.5, 180, ""),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new AnomalyVolumeSignalConfig(0D, 180, "IMOEX"),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new AnomalyVolumeSignalConfig(-1D, 180, "IMOEX"),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new AnomalyVolumeSignalConfig(1.5, 0, "IMOEX"),
            getInstruments().stream().map(Instrument::getId).toList()
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
            new AnomalyVolumeSignalConfig(1.5, -180, "IMOEX"),
            getInstruments().stream().map(Instrument::getId).toList()
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

        scheduleManager().executeSchedule();

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
        addScanner(
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(1.5, 180, "IMOEX"),
            getInstrumentsBy(tickers).map(Instrument::getId).toList()
        );
        scheduleManager().executeSchedule();
        assertEquals(2, signalPublisher().reports.get(0).getSignals().size());
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
            new AnomalyVolumeSignalConfig(1.5, 180, "IMOEX"),
            getInstrumentsBy(tickers).map(Instrument::getId).toList()
        );
        scheduleManager().executeSchedule();
        loggerProvider().clearLogs();
        initTodayDateTime("2023-12-22T13:00:30");

        scheduleManager().executeSchedule();

        assertEquals(4, loggerProvider().log.size());
        assertEquals(1, signalPublisher().reports.size());
        assertEquals(2, signalPublisher().reports.get(0).getSignals().size());
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
            new AnomalyVolumeSignalConfig(1.5, 180, "IMOEX"),
            getInstrumentsBy(tickers).map(Instrument::getId).toList()
        );
        scheduleManager().executeSchedule();
        loggerProvider().clearLogs();
        initTodayDateTime("2023-12-22T13:01:00");

        scheduleManager().executeSchedule();

        assertEquals(6, loggerProvider().log.size());
        assertEquals(2, signalPublisher().reports.size());
        assertEquals(2, signalPublisher().reports.get(0).getSignals().size());
        assertEquals(2, signalPublisher().reports.get(1).getSignals().size());
    }

    private void initTgknAndTgkbAndImoexHistoryTradingData() {
        initTradingResults(
            buildDealResultBy("TGKB", "2023-12-19", 99.D, 99.D,  1D, 1000D),
            buildDealResultBy("TGKB", "2023-12-20", 99.D, 99.D, 1D,1000D),
            buildDealResultBy("TGKB", "2023-12-21", 10.D, 10.D,1D, 1000D),
            buildDealResultBy("TGKN", "2023-12-19", 99.D, 99.D, 1D,1000D),
            buildDealResultBy("TGKN", "2023-12-20", 99.D, 99.D, 1D,1000D),
            buildDealResultBy("TGKN", "2023-12-21", 100.D, 100.D,1D, 1000D),
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
                buildDealBy(1L, "TGKB", "10:00:00", 100D,136926D, 1),
                buildDealBy(2L, "TGKB", "10:16:00", 100D,8736926D, 1),
                buildDealBy(3L, "TGKB", "11:00:00", 100D,8736926D, 1),
                buildDealBy(4L, "TGKB", "11:10:00", 100D, 8736926D, 1),
                buildDealBy(5L, "TGKB", "11:50:00", 102D, 873160926D, 1),
                //TGKN
                buildDealBy(1L,"TGKN", "10:00:00", 100D, 46912035D, 1),
                buildDealBy(2L,"TGKN", "10:03:00", 100D, 46912035D, 1),
                buildDealBy(3L,"TGKN", "11:00:00", 100D, 46912035D, 1),
                buildDealBy(4L,"TGKN", "11:01:00", 100D, 46912035D, 1),
                buildDealBy(5L,"TGKN", "11:45:00", 102D, 46912035D, 1)
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
