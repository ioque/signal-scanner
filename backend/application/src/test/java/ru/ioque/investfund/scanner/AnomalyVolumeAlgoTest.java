package ru.ioque.investfund.scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.configurator.entity.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.entity.AnomalyVolumeAlgorithmConfig;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
        final UUID datasourceId = getDatasourceId();
        initTgknAndTgkbAndImoex(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getTickers(datasourceId),
            new AnomalyVolumeAlgorithmConfig(
                null,
                180,
                "IMOEX"
            )
        ));

        assertEquals("Не передан параметр scaleCoefficient.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T2. В конфигурацию AnomalyVolumeSignalConfig не передан параметр historyPeriod.
        Результат: ошибка, текст ошибки: "Не передан параметр historyPeriod."
        """)
    void testCase2() {
        final UUID datasourceId = getDatasourceId();
        initTgknAndTgkbAndImoex(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getTickers(datasourceId),
            new AnomalyVolumeAlgorithmConfig(
                1.5,
                null,
                "IMOEX"
            )
        ));

        assertEquals("Не передан параметр historyPeriod.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В конфигурацию AnomalyVolumeSignalConfig не передан параметр indexTicker.
        Результат: ошибка, текст ошибки: "Не передан параметр indexTicker."
        """)
    void testCase3() {
        final UUID datasourceId = getDatasourceId();
        initTgknAndTgkbAndImoex(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getTickers(datasourceId),
            new AnomalyVolumeAlgorithmConfig(
                1.5,
                180,
                null
            )
        ));

        assertEquals("Не передан параметр indexTicker.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В конфигурацию AnomalyVolumeSignalConfig параметр indexTicker передан как пустая строка.
        Результат: ошибка, текст ошибки: "Не передан параметр indexTicker."
        """)
    void testCase4() {
        final UUID datasourceId = getDatasourceId();
        initTgknAndTgkbAndImoex(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getTickers(datasourceId),
            new AnomalyVolumeAlgorithmConfig(
                1.5,
                180,
                ""
            )
        ));

        assertEquals("Не передан параметр indexTicker.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T5. В конфигурацию AnomalyVolumeSignalConfig параметр scaleCoefficient передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр scaleCoefficient должен быть больше нуля."
        """)
    void testCase5() {
        final UUID datasourceId = getDatasourceId();
        initTgknAndTgkbAndImoex(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getTickers(datasourceId),
            new AnomalyVolumeAlgorithmConfig(
                0D,
                180,
                "IMOEX"
            )
        ));

        assertEquals("Параметр scaleCoefficient должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T6. В конфигурацию AnomalyVolumeSignalConfig параметр scaleCoefficient передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр scaleCoefficient должен быть больше нуля."
        """)
    void testCase6() {
        final UUID datasourceId = getDatasourceId();
        initTgknAndTgkbAndImoex(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getTickers(datasourceId),
            new AnomalyVolumeAlgorithmConfig(
                -1D,
                180,
                "IMOEX"
            )
        ));

        assertEquals("Параметр scaleCoefficient должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T7. В конфигурацию AnomalyVolumeSignalConfig параметр historyPeriod передан со значением = 0.
        Результат: ошибка, текст ошибки: "Параметр historyPeriod должен быть больше нуля."
        """)
    void testCase7() {
        final UUID datasourceId = getDatasourceId();
        initTgknAndTgkbAndImoex(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getTickers(datasourceId),
            new AnomalyVolumeAlgorithmConfig(
                1.5,
                0,
                "IMOEX"
            )
        ));

        assertEquals("Параметр historyPeriod должен быть больше нуля.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T8. В конфигурацию AnomalyVolumeSignalConfig параметр historyPeriod передан со значением < 0.
        Результат: ошибка, текст ошибки: "Параметр historyPeriod должен быть больше нуля."
        """)
    void testCase8() {
        final UUID datasourceId = getDatasourceId();
        initTgknAndTgkbAndImoex(datasourceId);

        var error = assertThrows(DomainException.class, () -> addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getTickers(datasourceId),
            new AnomalyVolumeAlgorithmConfig(
                1.5,
                -180,
                "IMOEX"
            )
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
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTgknAndTgkbAndImoexHistoryTradingData(datasourceId);
        initTgknAndTgkbAndImoexIntradayData(datasourceId);
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "TGKB", "IMOEX"
        );

        exchangeManager().execute();

        assertSignals(getSignals(), 2, 2, 0);
        assertFinInstrument(getTgkn(), 100.0, 102.0, 13000.0, 1150.0, 100.0, 99.0, true);
        assertFinInstrument(getTgkb(), 100.0, 102.0, 15000.0, 1500.0, 100.0, 99.0, true);
        assertFinInstrument(getImoex(), 2800.0, 3100.0, 2_200_000.0, 1_500_000.0, 3000.0, 2900.0, true);
    }

    @Test
    @DisplayName("""
        T10. С последнего запуска прошло меньше минуты, сканер не запущен.
        """)
    void testCase10() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTgknAndTgkbAndImoexHistoryTradingData(datasourceId);
        initTgknAndTgkbAndImoexIntradayData(datasourceId);
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "TGKB", "IMOEX"
        );
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-22T13:00:30");

        exchangeManager().execute();

        assertEquals(8, loggerProvider().log.size());
        assertSignals(getSignals(), 2, 2, 0);
        assertFinInstrument(getTgkn(), 100.0, 102.0, 13000.0, 1150.0, 100.0, 99.0, true);
        assertFinInstrument(getTgkb(), 100.0, 102.0, 15000.0, 1500.0, 100.0, 99.0, true);
        assertFinInstrument(getImoex(), 2800.0, 3100.0, 2_200_000.0, 1_500_000.0, 3000.0, 2900.0, true);
    }

    @Test
    @DisplayName("""
        T11. С последнего запуска прошла минута, сканер запущен.
        """)
    void testCase11() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTgknAndTgkbAndImoexHistoryTradingData(datasourceId);
        initTgknAndTgkbAndImoexIntradayData(datasourceId);
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "TGKB", "IMOEX"
        );
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-22T13:01:00");

        exchangeManager().execute();

        assertEquals(10, loggerProvider().log.size());
        assertSignals(getSignals(), 2, 2, 0);
        assertFinInstrument(getTgkn(), 100.0, 102.0, 13000.0, 1150.0, 100.0, 99.0, true);
        assertFinInstrument(getTgkb(), 100.0, 102.0, 15000.0, 1500.0, 100.0, 99.0, true);
        assertFinInstrument(getImoex(), 2800.0, 3100.0, 2_200_000.0, 1_500_000.0, 3000.0, 2900.0, true);
    }

    @Test
    @DisplayName("""
        T12. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Ранее был зарегистрирован сигнал к покупке. В текущем массиве данных
        объем торгов провышает медиану в scaleCoefficient-раз. Объем продаж превышает объем покупок.
        Результат: зарегистрирован сигнал к продаже. Сигнал к покупке закрыт.
        """)
    void testCase12() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTgknBuySignalDataset(datasourceId);
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );
        runWorkPipelineAndClearLogs();
        initTodayDateTime("2023-12-24T12:00:00");
        initTgknSellSignalDataset(datasourceId);

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 0, 1);
        assertFinInstrument(getTgkn(), 98.0, 96.0, 13000.0, 1450.0, 97.1, 99.1, false);
        assertFinInstrument(getImoex(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    @Test
    @DisplayName("""
        T13. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутри дня сделок по бумаге не было.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase13() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(datasourceId,"TGKN", "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(datasourceId,"TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-10", 99.D, 3000.0, 1_500_000.0),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-20", 99.D, 2900.0, 1_500_000.0),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-21", 100.D, 3000.0, 1_500_000.0)
        );
        initDealDatas(
            buildDeltaBy(datasourceId,1L, "IMOEX", "10:00:00", 3000.0, 1_000_000D),
            buildDeltaBy(datasourceId,2L, "IMOEX", "12:00:00", 2900.0, 2_000_000D)
        );
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFinInstrument(getTgkn(), null, null, null, 1000.0, 100.0, 99.0, null);
        assertFinInstrument(getImoex(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    @Test
    @DisplayName("""
        T14. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные не проинтегрированы, внутри дня сделок по бумаге не было.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase14() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTradingResults(
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-10", 3000.0, 3000.0, 1_500_000.0),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-20", 2900.0, 2900.0, 1_500_000.0),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-21", 3000.0, 3000.0, 1_500_000.0)
        );
        initDealDatas(
            buildDeltaBy(datasourceId,1L, "IMOEX", "10:00:00", 3000.0, 1_000_000D),
            buildDeltaBy(datasourceId,2L, "IMOEX", "12:00:00", 2900.0, 2_000_000D)
        );
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFinInstrument(getTgkn(), null, null, null, null, null, null, null);
        assertFinInstrument(getImoex(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    @Test
    @DisplayName("""
        T15. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутри дня была совершена одна сделка.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase15() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(datasourceId,"TGKN", "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(datasourceId,"TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-10", 2900.0, 2900.0, 1_500_000D),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-20", 2900.0, 2900.0, 1_500_000D),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-21", 3000.0, 3000.0, 1_500_000D)
        );
        initDealDatas(
            buildDeltaBy(datasourceId,1L, "IMOEX", "10:00:00", 3000.0, 1_000_000D),
            buildDeltaBy(datasourceId,2L, "IMOEX", "12:00:00", 2900.0, 2_000_000D),
            buildBuyDealBy(datasourceId,1L, "TGKN", "10:00:00", 100D, 469D, 1)
        );
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFinInstrument(getTgkn(), 100D, 100D, 469D, 1000D, 100.D, 99.D, false);
        assertFinInstrument(getImoex(), 3000.0, 2900.0, 3_000_000.0, 1_500_000.0, 3000.0, 2900.0, false);
    }

    @Test
    @DisplayName("""
        T16. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные не проинтегрированы, внутридневные данные проинтегрированы.
        Исторические данные по индексу есть, внутридневных данных по индексу нет.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase16() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTradingResults(
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-10", 2900.D, 2900.D, 1_500_000.0),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-20", 2900.D, 2900.D, 1_500_000.0),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-21", 3000.D, 3000.D, 1_500_000.0)
        );
        initDealDatas(
            buildBuyDealBy(datasourceId,1L, "TGKN", "10:00:00", 100D, 6000D, 1),
            buildBuyDealBy(datasourceId,2L, "TGKN", "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(datasourceId,3L, "TGKN", "11:00:00", 100D, 6000D, 1)
        );
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFinInstrument(getTgkn(), 100D, 100D, 13000D, null, null, null, null);
        assertFinInstrument(getImoex(), null, null, null, 1_500_000.0, 3000.0, 2900.0, null);
    }

    @Test
    @DisplayName("""
        T17. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные проинтегрированы.
        Исторических данных по индексу нет, внутридневные данные по индексу есть.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase17() {
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(getDatasourceId());
        initTradingResults(
            buildDealResultBy(getDatasourceId(),"TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(getDatasourceId(),"TGKN", "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(getDatasourceId(),"TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(getDatasourceId(),1L, "IMOEX", "10:00:00", 2900D, 1_000_000D),
            buildDeltaBy(getDatasourceId(),2L, "IMOEX", "12:00:00", 3000D, 2_000_000D),
            buildBuyDealBy(getDatasourceId(),1L, "TGKN", "10:00:00", 100D, 6000D, 1),
            buildBuyDealBy(getDatasourceId(),2L, "TGKN", "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(getDatasourceId(),3L, "TGKN", "11:00:00", 103D, 6000D, 1)
        );
        initScanner(
            getDatasourceId(),
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFinInstrument(getTgkn(), 100D, 103D, 13000D, 1000D, 100.D, 99.D, true);
        assertFinInstrument(getImoex(), 2900D, 3000D, 3_000_000D, null, null, null, null);
    }

    @Test
    @DisplayName("""
        T18. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные не проинтегрированы.
        Исторических данных по индексу нет, внутридневные данные по индексу есть.
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase18() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTradingResults(
            buildDealResultBy(datasourceId,"TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(datasourceId,"TGKN", "2023-12-20", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(datasourceId,"TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(datasourceId,1L, "IMOEX", "10:00:00", 2900D, 1_000_000D),
            buildDeltaBy(datasourceId,2L, "IMOEX", "12:00:00", 3000D, 2_000_000D)
        );
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );

        exchangeManager().execute();

        assertEquals(0, getSignals().size());
        assertSignals(getSignals(), 0, 0, 0);
        assertFinInstrument(getTgkn(), null, null, null, 1000D, 100.D, 99.D, null);
        assertFinInstrument(getImoex(), 2900D, 3000D, 3_000_000D, null, null, null, null);
    }

    @Test
    @DisplayName("""
        T19. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы за один день, внутридневные данные проинтегрированы, объем превышает объем за предыдущий день.
        Исторические и дневные данные по индексу проинтегрированы.
        Запускается сканер. Ошибок нет, сигнал есть.
        """)
    void testCase19() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTradingResults(
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-10", 2800D, 2800D, 1_000_000D),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-20", 2800D, 2800D, 1_000_000D),
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-21", 2800D, 2800D, 1_000_000D),
            buildDealResultBy(datasourceId,"TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(datasourceId,1L, "IMOEX", "10:00:00", 2900D, 1_000_000D),
            buildDeltaBy(datasourceId,2L, "IMOEX", "12:00:00", 3000D, 2_000_000D),
            buildBuyDealBy(datasourceId,1L, "TGKN", "10:00:00", 100D, 6000D, 1),
            buildBuyDealBy(datasourceId,2L, "TGKN", "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(datasourceId,3L, "TGKN", "11:00:00", 103D, 6000D, 1)
        );
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );

        exchangeManager().execute();

        assertSignals(getSignals(), 1, 1, 0);
        assertFinInstrument(getTgkn(), 100D, 103D, 13000D, 1000D, 100.D, null, true);
        assertFinInstrument(getImoex(), 2900D, 3000D, 3_000_000D, 1_000_000D, 2800D, 2800D, true);
    }

    @Test
    @DisplayName("""
        T20. Создан сканер сигналов AnomalyVolumeScannerSignal для инструмента TGKN.
        Исторические данные проинтегрированы, внутридневные данные проинтегрированы, объем превышает исторический.
        Исторические данные по индексу проинтегрированы за один день, дневные данные по индексу проинтегрированы
        Запускается сканер. Ошибок нет, сигналов нет.
        """)
    void testCase20() {
        final UUID datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initTgknAndTgkbAndImoex(datasourceId);
        initTradingResults(
            buildDeltaResultBy(datasourceId,"IMOEX", "2023-12-21", 2800D, 2800D, 1_000_000D),
            buildDealResultBy(datasourceId,"TGKN", "2023-12-21", 100.D, 100.D, 100D, 1000D)
        );
        initDealDatas(
            buildDeltaBy(datasourceId,1L, "IMOEX", "10:00:00", 2800D, 1_000_000D),
            buildDeltaBy(datasourceId,2L, "IMOEX", "12:00:00", 3000D, 2_000_000D),
            buildBuyDealBy(datasourceId,1L, "TGKN", "10:00:00", 100D, 6000D, 1),
            buildBuyDealBy(datasourceId,2L, "TGKN", "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(datasourceId,3L, "TGKN", "11:00:00", 100D, 6000D, 1)
        );
        initScanner(
            datasourceId,
            defaultConfiguration(),
            "TGKN", "IMOEX"
        );

        exchangeManager().execute();

        assertSignals(getSignals(), 0, 0, 0);
        assertFinInstrument(getTgkn(), 100D, 100D, 13000D, 1000D, 100.D, null, false);
        assertFinInstrument(getImoex(), 2800D, 3000D, 3_000_000D, 1_000_000D, 2800D, null, true);
    }

    private AnomalyVolumeAlgorithmConfig defaultConfiguration() {
        return new AnomalyVolumeAlgorithmConfig(
            1.5,
            180,
            "IMOEX"
        );
    }

    public void assertFinInstrument(
        TradingSnapshot tradingSnapshot,
        Double openPrice,
        Double lastPrice,
        Double todayValue,
        Double historyMedianValue,
        Double prevClosePrice,
        Double prevPrevClosePrice,
        Boolean isRiseToday
    ) {
        assertEquals(openPrice, tradingSnapshot.getTodayOpenPrice().orElse(null));
        assertEquals(lastPrice, tradingSnapshot.getTodayLastPrice().orElse(null));
        assertEquals(todayValue, tradingSnapshot.getTodayValue().orElse(null));
        assertEquals(historyMedianValue, tradingSnapshot.getHistoryMedianValue().orElse(null));
        assertEquals(prevClosePrice, tradingSnapshot.getPrevClosePrice().orElse(null));
        assertEquals(prevPrevClosePrice, tradingSnapshot.getPrevPrevClosePrice().orElse(null));
        assertEquals(isRiseToday, tradingSnapshot.isRiseToday().orElse(null));
    }

    private void initTgknBuySignalDataset(UUID datasourceId) {
        initTradingResults(
            buildDealResultBy(datasourceId, "TGKN", "2023-12-19", 99.D, 99.D, 99D, 1000D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-20", 99.D, 99.D, 99D, 2000D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-21", 100.D, 100.D, 100D, 1400D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-10", 2900D, 2900D, 1_000_000D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-20", 2900D, 2900D, 1_500_000D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-21", 3000D, 3000D, 2_000_000D)
        );
        initDealDatas(
            buildDeltaBy(datasourceId, 1L, "IMOEX", "10:00:00", 2800D, 100D),
            buildDeltaBy(datasourceId, 2L, "IMOEX", "12:00:00", 3200D, 200D),
            buildBuyDealBy(datasourceId, 1L, "TGKN", "10:00:00", 100D, 5000D, 1),
            buildBuyDealBy(datasourceId, 2L, "TGKN", "10:03:00", 100D, 1000D, 1),
            buildSellDealBy(datasourceId, 3L, "TGKN", "11:00:00", 100D, 1000D, 1),
            buildBuyDealBy(datasourceId, 4L, "TGKN", "11:01:00", 100D, 1000D, 1),
            buildBuyDealBy(datasourceId, 5L, "TGKN", "11:45:00", 102D, 5000D, 1)
        );
    }

    private void initTgknSellSignalDataset(UUID datasourceId) {
        initTradingResults(
            buildDealResultBy(datasourceId, "TGKN", "2023-12-22", 99.D, 99.1D, 97D, 2000D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-23", 99.D, 99.1D, 97D, 1000D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-24", 97.2D, 97.1D, 97D, 1500D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-22", 2900D, 2900D, 1_000_000D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-23", 2900D, 2900D, 1_500_000D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initDealDatas(
            buildDeltaBy(datasourceId, 3L, "IMOEX", "10:00:00", 3000D, 1_000_000D),
            buildDeltaBy(datasourceId, 4L, "IMOEX", "12:00:00", 2900D, 2_000_000D),
            buildBuyDealBy(datasourceId, 6L, "TGKN", "10:00:00", 98D, 5000D, 1),
            buildSellDealBy(datasourceId, 7L, "TGKN", "10:03:00", 97D, 1000D, 1),
            buildSellDealBy(datasourceId, 8L, "TGKN", "11:00:00", 98D, 1000D, 1),
            buildSellDealBy(datasourceId, 9L, "TGKN", "11:01:00", 97D, 1000D, 1),
            buildSellDealBy(datasourceId, 10L, "TGKN", "11:45:00", 96D, 5000D, 1)
        );
    }

    private void initTgknAndTgkbAndImoexHistoryTradingData(UUID datasourceId) {
        initTradingResults(
            buildDealResultBy(datasourceId, "TGKB", "2023-12-19", 99.D, 99.D, 1D, 2000D),
            buildDealResultBy(datasourceId, "TGKB", "2023-12-20", 99.D, 99.D, 1D, 1000D),
            buildDealResultBy(datasourceId, "TGKB", "2023-12-21", 100.D, 100.D, 1D, 1500D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-19", 99.D, 99.D, 1D, 3000D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-20", 99.D, 99.D, 1D, 1150D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-21", 100.D, 100.D, 1D, 1100D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-10", 2800D, 2900D, 1_000_000D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-20", 2800D, 2900D, 1_500_000D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-21", 2900D, 3000D, 2_000_000D)
        );
    }

    private void initTgknAndTgkbAndImoexIntradayData(UUID datasourceId) {
        exchangeDataFixture().initDealDatas(
            List.of(
                buildDeltaBy(datasourceId, 1L, "IMOEX", "10:00:00", 2800D, 1_000_000D),
                buildDeltaBy(datasourceId, 2L, "IMOEX", "12:00:00", 3100D, 1_200_000D),
                //TGKB
                buildBuyDealBy(datasourceId, 1L, "TGKB", "10:00:00", 100D, 6000D, 1),
                buildBuyDealBy(datasourceId, 2L, "TGKB", "10:16:00", 100D, 1000D, 1),
                buildBuyDealBy(datasourceId, 3L, "TGKB", "11:00:00", 100D, 1000D, 1),
                buildBuyDealBy(datasourceId, 4L, "TGKB", "11:10:00", 100D, 1000D, 1),
                buildBuyDealBy(datasourceId, 5L, "TGKB", "11:50:00", 102D, 6000D, 1),
                //TGKN
                buildBuyDealBy(datasourceId, 1L, "TGKN", "10:00:00", 100D, 5000D, 1),
                buildBuyDealBy(datasourceId, 2L, "TGKN", "10:03:00", 100D, 1000D, 1),
                buildBuyDealBy(datasourceId, 3L, "TGKN", "11:00:00", 100D, 1000D, 1),
                buildBuyDealBy(datasourceId, 4L, "TGKN", "11:01:00", 100D, 1000D, 1),
                buildBuyDealBy(datasourceId, 5L, "TGKN", "11:45:00", 102D, 5000D, 1)
            )
        );
    }

    private void initTgknAndTgkbAndImoex(UUID datasourceId) {
        exchangeDataFixture().initInstruments(
            List.of(
                imoex(),
                tgkb(),
                tgkn()
            )
        );
        exchangeManager().integrateInstruments(datasourceId);
        exchangeManager().enableUpdate(datasourceId, getTickers(datasourceId));
    }

    private void initScanner(UUID datasourceId, AlgorithmConfig configurator, String... tickers) {
        addScanner(
            1,
            "Аномальные объемы, третий эшелон.",
            datasourceId,
            getInstrumentsBy(datasourceId, Arrays.asList(tickers)).map(Instrument::getTicker).toList(),
            configurator
        );
    }
}
