package ru.ioque.apitest.modules.datascanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.apitest.DatasourceEmulatedTest;
import ru.ioque.core.datagenerator.config.ContractsGeneratorConfig;
import ru.ioque.core.datagenerator.config.DealsGeneratorConfig;
import ru.ioque.core.datagenerator.config.DeltasGeneratorConfig;
import ru.ioque.core.datagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.datagenerator.core.PercentageGrowths;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.intraday.Deal;
import ru.ioque.core.datagenerator.intraday.IntradayValue;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dataset.DefaultInstrumentSet;
import ru.ioque.core.dto.datasource.request.DatasourceRequest;
import ru.ioque.core.dto.risk.response.EmulatedPositionResponse;
import ru.ioque.core.dto.scanner.request.AnomalyVolumePropertiesDto;
import ru.ioque.core.dto.scanner.request.CreateScannerRequest;
import ru.ioque.core.dto.scanner.request.PrefSimplePropertiesDto;
import ru.ioque.core.dto.scanner.request.SectoralFuturesPropertiesDto;
import ru.ioque.core.dto.scanner.request.SectoralRetardPropertiesDto;
import ru.ioque.core.dto.scanner.response.SignalResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("МОДУЛЬ \"СКАНЕР ДАННЫХ\"")
public class ScannerAcceptanceTest extends DatasourceEmulatedTest {
    @BeforeEach
    void initDateTime() {
        initDateTime(getDateTimeNow());
        datasourceClient().createDatasource(
            DatasourceRequest.builder()
                .name("Московская Биржа")
                .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
                .url(datasourceHost)
                .build()
        );
    }

    private static LocalDateTime getDateTimeNow() {
        return LocalDateTime.parse("2024-03-04T11:00:00");
    }

    @Test
    @DisplayName("""
        T1. Создание сканера сигналов с алгоритмом "Аномальные объемы".
        """)
    void testCase1() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.imoex(), DefaultInstrumentSet.sber()))
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);

        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .properties(
                    AnomalyVolumePropertiesDto.builder()
                        .historyPeriod(180)
                        .scaleCoefficient(1.5)
                        .indexTicker("IMOEX")
                        .build()
                )
                .build()
        );

        assertEquals(1, getSignalScanners().size());
    }

    @Test
    @DisplayName("""
        T2. Создание сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка".
        """)
    void testCase2() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.sber(), DefaultInstrumentSet.sberp()))
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);

        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .description("desc")
                .properties(new PrefSimplePropertiesDto(1.0))
                .build()
        );

        assertEquals(1, getSignalScanners().size());
    }

    @Test
    @DisplayName("""
        T3. Создание сканера сигналов с алгоритмом "Секторальный отстающий".
        """)
    void testCase3() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        initDataset(
            Dataset.builder()
                .instruments(List.of(
                    DefaultInstrumentSet.sibn(),
                    DefaultInstrumentSet.lkoh(),
                    DefaultInstrumentSet.tatn(),
                    DefaultInstrumentSet.rosn()
                ))
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);

        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .description("desc")
                .properties(
                    SectoralRetardPropertiesDto.builder()
                        .historyScale(0.015)
                        .intradayScale(0.015)
                        .build()
                )
                .build()
        );

        assertEquals(1, getSignalScanners().size());
    }

    @Test
    @DisplayName("""
        T4. Создание сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора".
        """)
    void testCase4() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        initDataset(
            Dataset.builder()
                .instruments(List.of(
                    DefaultInstrumentSet.sibn(),
                    DefaultInstrumentSet.lkoh(),
                    DefaultInstrumentSet.tatn(),
                    DefaultInstrumentSet.rosn(),
                    DefaultInstrumentSet.brf4()
                ))
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);

        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .description("desc")
                .properties(
                    SectoralFuturesPropertiesDto.builder()
                        .futuresTicker("BRF4")
                        .futuresOvernightScale(0.015)
                        .stockOvernightScale(0.015)
                        .build()
                )
                .build()
        );

        assertEquals(1, getSignalScanners().size());
    }

    @Test
    @DisplayName("""
        T5. Запуск сканера сигналов с алгоритмом "Аномальные объемы", в торговых данных есть сигнал к покупке.
        """)
    void testCase5() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        LocalDateTime now = getDateTimeNow();
        LocalDate startDate = now.toLocalDate().minusMonths(1);
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.imoex(), DefaultInstrumentSet.tgkn()))
                .historyValues(
                    Stream.concat(
                            generator()
                                .generateHistory(
                                    HistoryGeneratorConfig
                                        .builder()
                                        .ticker("TGKN")
                                        .startClose(10.)
                                        .startOpen(10.)
                                        .startValue(1_000_000D)
                                        .days(30)
                                        .startDate(startDate)
                                        .openPricePercentageGrowths(List.of(
                                            new PercentageGrowths(50D, 0.5),
                                            new PercentageGrowths(-50D, 0.5)
                                        ))
                                        .closePricePercentageGrowths(List.of(
                                            new PercentageGrowths(50D, 0.5),
                                            new PercentageGrowths(-50D, 0.5)
                                        ))
                                        .valuePercentageGrowths(List.of(
                                            new PercentageGrowths(50D, 0.5),
                                            new PercentageGrowths(-50D, 0.5)
                                        ))
                                        .build()
                                )
                                .stream(),
                            generator()
                                .generateHistory(
                                    HistoryGeneratorConfig
                                        .builder()
                                        .ticker("IMOEX")
                                        .startClose(10.)
                                        .startOpen(10.)
                                        .startValue(2_000_000D)
                                        .days(30)
                                        .startDate(startDate)
                                        .openPricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                                        .closePricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                                        .valuePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                                        .build()
                                )
                                .stream()
                        )
                        .toList()
                )
                .intradayValues(
                    Stream
                        .concat(
                            generator().generateDeltas(
                                DeltasGeneratorConfig
                                    .builder()
                                    .ticker("IMOEX")
                                    .numTrades(2000)
                                    .startPrice(10.)
                                    .startValue(200_000D)
                                    .date(now.toLocalDate())
                                    .startTime(LocalTime.parse("10:00"))
                                    .pricePercentageGrowths(List.of(new PercentageGrowths(80D, 1D)))
                                    .valuePercentageGrowths(List.of(new PercentageGrowths(200D, 1D)))
                                    .build()
                            ).stream(),
                            generator().generateDeals(
                                DealsGeneratorConfig
                                    .builder()
                                    .ticker("TGKN")
                                    .numTrades(2000)
                                    .startPrice(10.)
                                    .startValue(200_000D)
                                    .date(now.toLocalDate())
                                    .startTime(LocalTime.parse("10:00"))
                                    .pricePercentageGrowths(List.of(new PercentageGrowths(80D, 1D)))
                                    .valuePercentageGrowths(List.of(new PercentageGrowths(200D, 1D)))
                                    .build()
                            ).stream()
                        )
                        .toList()
                )
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));
        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .datasourceId(datasourceId)
                .description("desc")
                .tickers(getTickers(datasourceId))
                .properties(
                    AnomalyVolumePropertiesDto.builder()
                        .historyPeriod(20)
                        .scaleCoefficient(1.5)
                        .indexTicker("IMOEX")
                        .build()
                )
                .build()
        );
        integrateTradingData(datasourceId);

        assertTrue(waitOpenEmulatedPositions(1));
        assertEquals(1, getSignalsBy(getFirstScannerId()).size());
        final SignalResponse signal = getSignalsBy(getFirstScannerId()).get(0);
        assertEquals("TGKN", signal.getTicker());
        assertTrue(signal.getIsBuy());
        final EmulatedPositionResponse emulatedPosition = getOpenEmulatedPositions().get(0);
        assertEquals("TGKN", emulatedPosition.getTicker());
        assertNotNull(emulatedPosition.getOpenPrice());
        assertNotNull(emulatedPosition.getLastPrice());
        assertNotNull(emulatedPosition.getProfit());
        assertNull(emulatedPosition.getClosePrice());
    }

    @Test
    @DisplayName("""
        T6. Запуск сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка", в торговых данных есть сигнал к покупке.
        """)
    void testCase6() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        LocalDateTime now = getDateTimeNow();
        LocalDate startDate = now.toLocalDate().minusMonths(1);
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.sber(), DefaultInstrumentSet.sberp()))
                .intradayValues(
                    Stream
                        .concat(
                            generator().generateDeals(
                                DealsGeneratorConfig
                                    .builder()
                                    .ticker("SBER")
                                    .numTrades(2000)
                                    .startPrice(245.)
                                    .startValue(200_000D)
                                    .date(now.toLocalDate())
                                    .startTime(LocalTime.parse("10:00"))
                                    .pricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                                    .valuePercentageGrowths(List.of(new PercentageGrowths(2D, 1D)))
                                    .build()
                            ).stream(),
                            generator().generateDeals(
                                DealsGeneratorConfig
                                    .builder()
                                    .ticker("SBERP")
                                    .numTrades(2000)
                                    .startPrice(242.)
                                    .startValue(200_000D)
                                    .date(now.toLocalDate())
                                    .startTime(LocalTime.parse("10:00"))
                                    .pricePercentageGrowths(List.of(new PercentageGrowths(-15D, 1D)))
                                    .valuePercentageGrowths(List.of(new PercentageGrowths(-2D, 1D)))
                                    .build()
                            ).stream()
                        )
                        .toList()
                )
                .historyValues(
                    Stream.concat(
                            generator()
                                .generateHistory(
                                    HistoryGeneratorConfig
                                        .builder()
                                        .ticker("SBER")
                                        .startClose(245.)
                                        .startOpen(242.)
                                        .startValue(1_000_000D)
                                        .days(30)
                                        .startDate(startDate)
                                        .openPricePercentageGrowths(List.of(new PercentageGrowths(50D, 1D)))
                                        .closePricePercentageGrowths(List.of(new PercentageGrowths(50D, 1D)))
                                        .valuePercentageGrowths(List.of(new PercentageGrowths(50D, 1D)))
                                        .build()
                                )
                                .stream(),
                            generator()
                                .generateHistory(
                                    HistoryGeneratorConfig
                                        .builder()
                                        .ticker("SBERP")
                                        .startClose(242.)
                                        .startOpen(239.)
                                        .startValue(1_000_000D)
                                        .days(30)
                                        .startDate(startDate)
                                        .openPricePercentageGrowths(List.of(new PercentageGrowths(25D, 1D)))
                                        .closePricePercentageGrowths(List.of(new PercentageGrowths(25D, 1D)))
                                        .valuePercentageGrowths(List.of(new PercentageGrowths(25D, 1D)))
                                        .build()
                                )
                                .stream()
                        )
                        .toList()
                )
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));

        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .properties(new PrefSimplePropertiesDto(1.0))
                .build()
        );
        integrateTradingData(datasourceId);

        assertTrue(waitOpenEmulatedPositions(1));
        assertEquals(1, getSignalsBy(getFirstScannerId()).size());
        final SignalResponse signal = getSignalsBy(getFirstScannerId()).get(0);
        assertEquals("SBERP", signal.getTicker());
        assertTrue(signal.getIsBuy());
        final EmulatedPositionResponse emulatedPosition = getOpenEmulatedPositions().get(0);
        assertEquals("SBERP", emulatedPosition.getTicker());
        assertNotNull(emulatedPosition.getOpenPrice());
        assertNotNull(emulatedPosition.getLastPrice());
        assertNotNull(emulatedPosition.getProfit());
        assertNull(emulatedPosition.getClosePrice());
    }

    private UUID getFirstScannerId() {
        return getSignalScanners().get(0).getId();
    }

    @Test
    @DisplayName("""
        T7. Запуск сканера сигналов с алгоритмом "Секторальный отстающий", в торговых данных есть сигнал к покупке.
        """)
    void testCase7() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        LocalDateTime now = getDateTimeNow();
        List<HistoryValue> historyValues = new ArrayList<>();
        historyValues.addAll(generator().generateHistory(HistoryGeneratorConfig
            .builder()
            .ticker("SIBN")
            .startClose(105.)
            .startOpen(100.)
            .startValue(1_000_000D)
            .days(10)
            .startDate(now.minusDays(10).toLocalDate())
            .openPricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .build()));
        historyValues.addAll(generator().generateHistory(HistoryGeneratorConfig
            .builder()
            .ticker("LKOH")
            .startClose(105.)
            .startOpen(100.)
            .startValue(1_000_000D)
            .days(10)
            .startDate(now.minusDays(10).toLocalDate())
            .openPricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .build()));
        historyValues.addAll(generator().generateHistory(HistoryGeneratorConfig
            .builder()
            .ticker("TATN")
            .startClose(105.)
            .startOpen(100.)
            .startValue(1_000_000D)
            .days(10)
            .startDate(now.minusDays(10).toLocalDate())
            .openPricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .build()));
        historyValues.addAll(generator().generateHistory(HistoryGeneratorConfig
            .builder()
            .ticker("ROSN")
            .startClose(105.)
            .startOpen(100.)
            .startValue(1_000_000D)
            .days(10)
            .startDate(now.minusDays(10).toLocalDate())
            .openPricePercentageGrowths(List.of(new PercentageGrowths(-10D, 1D)))
            .closePricePercentageGrowths(List.of(new PercentageGrowths(-20D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(-15D, 1D)))
            .build()));
        List<IntradayValue> intradayValues = new ArrayList<>();
        intradayValues.addAll(generator().generateDeals(DealsGeneratorConfig
            .builder()
            .ticker("SIBN")
            .numTrades(2000)
            .startPrice(200.)
            .startValue(200_000D)
            .date(now.toLocalDate())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(2D, 1D)))
            .build()));
        intradayValues.addAll(generator().generateDeals(DealsGeneratorConfig
            .builder()
            .ticker("LKOH")
            .numTrades(2000)
            .startPrice(200.)
            .startValue(200_000D)
            .date(now.toLocalDate())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(2D, 1D)))
            .build()));
        intradayValues.addAll(generator().generateDeals(DealsGeneratorConfig
            .builder()
            .ticker("TATN")
            .numTrades(2000)
            .startPrice(200.)
            .startValue(200_000D)
            .date(now.toLocalDate())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(2D, 1D)))
            .build()));
        intradayValues.addAll(generator().generateDeals(DealsGeneratorConfig
            .builder()
            .ticker("ROSN")
            .numTrades(2000)
            .startPrice(70.)
            .startValue(200_000D)
            .date(now.toLocalDate())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(-1D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(-1D, 1D)))
            .build()));
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.sibn(),
                        DefaultInstrumentSet.lkoh(),
                        DefaultInstrumentSet.tatn(),
                        DefaultInstrumentSet.rosn()
                    )
                )
                .intradayValues(intradayValues)
                .historyValues(historyValues)
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));

        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .description("desc")
                .properties(
                    SectoralRetardPropertiesDto.builder()
                        .historyScale(0.015)
                        .intradayScale(0.015)
                        .build()
                )
                .build()
        );
        integrateTradingData(datasourceId);

        assertTrue(waitOpenEmulatedPositions(1));
        assertEquals(1, getSignalsBy(getFirstScannerId()).size());
        final SignalResponse signal = getSignalsBy(getFirstScannerId()).get(0);
        assertEquals("ROSN", signal.getTicker());
        assertTrue(signal.getIsBuy());
        final EmulatedPositionResponse emulatedPosition = getOpenEmulatedPositions().get(0);
        assertEquals("ROSN", emulatedPosition.getTicker());
        assertNotNull(emulatedPosition.getOpenPrice());
        assertNotNull(emulatedPosition.getLastPrice());
        assertNotNull(emulatedPosition.getProfit());
        assertNull(emulatedPosition.getClosePrice());
    }

    @Test
    @DisplayName("""
        T8. Запуск сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора", в торговых данных есть сигнал к покупке.
        """)
    void testCase8() {
        UUID datasourceId = getAllDatasource().get(0).getId();
        LocalDateTime now = getDateTimeNow();
        List<HistoryValue> historyValues = new ArrayList<>();
        historyValues.addAll(generator().generateHistory(HistoryGeneratorConfig
            .builder()
            .ticker("SIBN")
            .startClose(105.)
            .startOpen(100.)
            .startValue(1_000_000D)
            .days(10)
            .startDate(now.minusDays(10).toLocalDate())
            .openPricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .build()));
        historyValues.addAll(generator().generateHistory(HistoryGeneratorConfig
            .builder()
            .ticker("BRF4")
            .startClose(105.)
            .startOpen(100.)
            .startValue(1_000_000D)
            .days(10)
            .startDate(now.minusDays(10).toLocalDate())
            .openPricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .closePricePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(15D, 1D)))
            .build()));
        List<IntradayValue> intradayValues = new ArrayList<>();
        intradayValues.addAll(generator().generateDeals(DealsGeneratorConfig
            .builder()
            .ticker("SIBN")
            .numTrades(2000)
            .startPrice(120.)
            .startValue(200_000D)
            .date(now.toLocalDate())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
            .valuePercentageGrowths(List.of(new PercentageGrowths(2D, 1D)))
            .build()));
        intradayValues.addAll(generator().generateContracts(ContractsGeneratorConfig
            .builder()
            .ticker("BRF4")
            .numTrades(2000)
            .startPrice(120.)
            .date(now.toLocalDate())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
            .build()));
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.sibn(), DefaultInstrumentSet.brf4()))
                .historyValues(historyValues)
                .intradayValues(intradayValues)
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));


        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .datasourceId(datasourceId)
                .tickers(getTickers(datasourceId))
                .description("desc")
                .properties(
                    SectoralFuturesPropertiesDto.builder()
                        .futuresTicker("BRF4")
                        .futuresOvernightScale(0.015)
                        .stockOvernightScale(0.015)
                        .build()
                )
                .build()
        );
        integrateTradingData(datasourceId);

        assertTrue(waitOpenEmulatedPositions(1));
        assertEquals(1, getSignalsBy(getFirstScannerId()).size());
        final SignalResponse signal = getSignalsBy(getFirstScannerId()).get(0);
        assertEquals("SIBN", signal.getTicker());
        assertTrue(signal.getIsBuy());
        final EmulatedPositionResponse emulatedPosition = getOpenEmulatedPositions().get(0);
        assertEquals("SIBN", emulatedPosition.getTicker());
        assertNotNull(emulatedPosition.getOpenPrice());
        assertNotNull(emulatedPosition.getLastPrice());
        assertNotNull(emulatedPosition.getProfit());
        assertNull(emulatedPosition.getClosePrice());
    }

    @Test
    @DisplayName("""
        T9. Зафиксирован сигнал к покупке, после чего зафиксирован сигнал к продаже.
        """)
    void testCase9() {
        final UUID datasourceId = getAllDatasource().get(0).getId();
        initDateTime(LocalDateTime.parse("2024-03-21T13:00:00"));
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.tgkn(), DefaultInstrumentSet.imoex()))
                .historyValues(
                    List.of(
                        HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3050D).lowPrice(2700D).openPrice(2800D).closePrice(3000D).tradeDate(LocalDate.parse("2024-03-18")).build(),
                        HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3150D).lowPrice(2800D).openPrice(2900D).closePrice(3100D).tradeDate(LocalDate.parse("2024-03-19")).build(),
                        HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3250D).lowPrice(2900D).openPrice(3000D).closePrice(3200D).tradeDate(LocalDate.parse("2024-03-20")).build(),
                        HistoryValue.builder().ticker("TGKN").value(700_000D).highPrice(98D).lowPrice(98D).openPrice(98D).closePrice(98D).tradeDate(LocalDate.parse("2024-03-18")).build(),
                        HistoryValue.builder().ticker("TGKN").value(600_000D).highPrice(99D).lowPrice(99D).openPrice(99D).closePrice(99D).tradeDate(LocalDate.parse("2024-03-19")).build(),
                        HistoryValue.builder().ticker("TGKN").value(1_100_000D).highPrice(100D).lowPrice(100D).openPrice(100D).closePrice(100D).tradeDate(LocalDate.parse("2024-03-20")).build()
                    )
                )
                .intradayValues(
                    List.of(
                        Deal.builder().number(1L).ticker("IMOEX").qnt(1).isBuy(true).price(3300D).value(1_000_000D).dateTime(LocalDateTime.parse("2024-03-21T10:00:00")).build(),
                        Deal.builder().number(2L).ticker("IMOEX").qnt(1).isBuy(true).price(3400D).value(2_000_000D).dateTime(LocalDateTime.parse("2024-03-21T11:00:00")).build(),
                        Deal.builder().number(1L).ticker("TGKN").qnt(100).isBuy(true).price(100D).value(700_000D).dateTime(LocalDateTime.parse("2024-03-21T10:00:00")).build(),
                        Deal.builder().number(2L).ticker("TGKN").qnt(100).isBuy(true).price(102D).value(600_000D).dateTime(LocalDateTime.parse("2024-03-21T11:00:00")).build()
                    )
                )
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));
        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .datasourceId(datasourceId)
                .description("desc")
                .tickers(getTickers(datasourceId))
                .properties(
                    AnomalyVolumePropertiesDto.builder()
                        .historyPeriod(3)
                        .scaleCoefficient(1.5)
                        .indexTicker("IMOEX")
                        .build()
                )
                .build()
        );
        integrateTradingData(datasourceId);
        assertTrue(waitOpenEmulatedPositions(1));
        assertEquals(1, getSignalsBy(getFirstScannerId()).size());

        initDateTime(LocalDateTime.parse("2024-03-22T13:00:00"));
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.tgkn(), DefaultInstrumentSet.imoex()))
                .historyValues(
                    List.of(
                        HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3450D).lowPrice(2900D).openPrice(2900D).closePrice(3300D).tradeDate(LocalDate.parse("2024-03-21")).build(),
                        HistoryValue.builder().ticker("TGKN").value(1_200_000D).highPrice(102D).lowPrice(102D).openPrice(102D).closePrice(102D).tradeDate(LocalDate.parse("2024-03-21")).build()
                    )
                )
                .intradayValues(
                    List.of(
                        Deal.builder().number(3L).ticker("IMOEX").qnt(1).isBuy(true).price(3000D).value(1_000_000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build(),
                        Deal.builder().number(4L).ticker("IMOEX").qnt(1).isBuy(true).price(2900D).value(2_000_000D).dateTime(LocalDateTime.parse("2024-03-22T11:00:00")).build(),
                        Deal.builder().number(3L).ticker("TGKN").qnt(100).isBuy(false).price(101D).value(700_000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build(),
                        Deal.builder().number(4L).ticker("TGKN").qnt(100).isBuy(false).price(99D).value(900_000D).dateTime(LocalDateTime.parse("2024-03-22T11:00:00")).build()
                    )
                )
                .build()
        );
        integrateTradingData(datasourceId);
        assertTrue(waitOpenEmulatedPositions(0));
        assertEquals(1, getSignalsBy(getFirstScannerId()).size());
        final SignalResponse signal = getSignalsBy(getFirstScannerId()).get(0);
        assertEquals("TGKN", signal.getTicker());
        assertFalse(signal.getIsBuy());
    }

    @Test
    @DisplayName("""
        T10. Зафиксирован сигнал к покупке, после чего найден повторный сигнал к продаже.
        """)
    void testCase10() {
        final UUID datasourceId = getAllDatasource().get(0).getId();
        initDateTime(LocalDateTime.parse("2024-03-21T13:00:00"));
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.tgkn(), DefaultInstrumentSet.imoex()))
                .historyValues(
                    List.of(
                        HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3050D).lowPrice(2700D).openPrice(2800D).closePrice(3000D).tradeDate(LocalDate.parse("2024-03-18")).build(),
                        HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3150D).lowPrice(2800D).openPrice(2900D).closePrice(3100D).tradeDate(LocalDate.parse("2024-03-19")).build(),
                        HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3250D).lowPrice(2900D).openPrice(3000D).closePrice(3200D).tradeDate(LocalDate.parse("2024-03-20")).build(),
                        HistoryValue.builder().ticker("TGKN").value(700_000D).highPrice(98D).lowPrice(98D).openPrice(98D).closePrice(98D).tradeDate(LocalDate.parse("2024-03-18")).build(),
                        HistoryValue.builder().ticker("TGKN").value(600_000D).highPrice(99D).lowPrice(99D).openPrice(99D).closePrice(99D).tradeDate(LocalDate.parse("2024-03-19")).build(),
                        HistoryValue.builder().ticker("TGKN").value(1_100_000D).highPrice(100D).lowPrice(100D).openPrice(100D).closePrice(100D).tradeDate(LocalDate.parse("2024-03-20")).build()
                    )
                )
                .intradayValues(
                    List.of(
                        Deal.builder().number(1L).ticker("IMOEX").qnt(1).isBuy(true).price(3300D).value(1_000_000D).dateTime(LocalDateTime.parse("2024-03-21T10:00:00")).build(),
                        Deal.builder().number(2L).ticker("IMOEX").qnt(1).isBuy(true).price(3400D).value(2_000_000D).dateTime(LocalDateTime.parse("2024-03-21T11:00:00")).build(),
                        Deal.builder().number(1L).ticker("TGKN").qnt(100).isBuy(true).price(100D).value(700_000D).dateTime(LocalDateTime.parse("2024-03-21T10:00:00")).build(),
                        Deal.builder().number(2L).ticker("TGKN").qnt(100).isBuy(true).price(102D).value(600_000D).dateTime(LocalDateTime.parse("2024-03-21T11:00:00")).build()
                    )
                )
                .build()
        );
        integrateAllInstrumentFrom(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));
        createScanner(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .datasourceId(datasourceId)
                .description("desc")
                .tickers(getTickers(datasourceId))
                .properties(
                    AnomalyVolumePropertiesDto.builder()
                        .historyPeriod(3)
                        .scaleCoefficient(1.5)
                        .indexTicker("IMOEX")
                        .build()
                )
                .build()
        );
        integrateTradingData(datasourceId);
        assertTrue(waitOpenEmulatedPositions(1));
        assertEquals(1, getSignalsBy(getFirstScannerId()).size());

        initDateTime(LocalDateTime.parse("2024-03-22T13:00:00"));
        initDataset(
            Dataset.builder()
                .instruments(List.of(DefaultInstrumentSet.tgkn(), DefaultInstrumentSet.imoex()))
                .historyValues(
                    List.of(
                        HistoryValue.builder().ticker("IMOEX").value(300_000_000D).highPrice(3450D).lowPrice(2900D).openPrice(2900D).closePrice(3300D).tradeDate(LocalDate.parse("2024-03-21")).build(),
                        HistoryValue.builder().ticker("TGKN").value(1_200_000D).highPrice(102D).lowPrice(102D).openPrice(102D).closePrice(102D).tradeDate(LocalDate.parse("2024-03-21")).build()
                    )
                )
                .intradayValues(
                    List.of(
                        Deal.builder().number(3L).ticker("IMOEX").qnt(1).isBuy(true).price(3400D).value(1_000_000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build(),
                        Deal.builder().number(4L).ticker("IMOEX").qnt(1).isBuy(true).price(3500D).value(2_000_000D).dateTime(LocalDateTime.parse("2024-03-22T11:00:00")).build(),
                        Deal.builder().number(3L).ticker("TGKN").qnt(100).isBuy(false).price(103D).value(700_000D).dateTime(LocalDateTime.parse("2024-03-22T10:00:00")).build(),
                        Deal.builder().number(4L).ticker("TGKN").qnt(100).isBuy(false).price(104D).value(900_000D).dateTime(LocalDateTime.parse("2024-03-22T11:00:00")).build()
                    )
                )
                .build()
        );
        integrateTradingData(datasourceId);
        assertTrue(waitScanningFinishedEvent(LocalDateTime.parse("2024-03-22T13:00:00")));
        assertEquals(1, getSignalsBy(getFirstScannerId()).size());
        assertEquals(1, getSignalsBy(getFirstScannerId()).stream().filter(SignalResponse::getIsBuy).count());
    }
}
