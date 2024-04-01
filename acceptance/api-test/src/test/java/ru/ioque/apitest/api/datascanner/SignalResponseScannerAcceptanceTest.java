package ru.ioque.apitest.api.datascanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.api.BaseApiAcceptanceTest;
import ru.ioque.core.dataset.DefaultDataset;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.intraday.IntradayValue;
import ru.ioque.core.datagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.datagenerator.core.PercentageGrowths;
import ru.ioque.core.datagenerator.config.ContractsGeneratorConfig;
import ru.ioque.core.datagenerator.config.DeltasGeneratorConfig;
import ru.ioque.core.datagenerator.config.DealsGeneratorConfig;
import ru.ioque.core.dto.scanner.request.AnomalyVolumeScannerRequest;
import ru.ioque.core.dto.scanner.request.CorrelationSectoralScannerRequest;
import ru.ioque.core.dto.scanner.request.PrefSimpleRequest;
import ru.ioque.core.dto.scanner.request.SectoralRetardScannerRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("МОДУЛЬ \"СКАНЕР ДАННЫХ\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SignalResponseScannerAcceptanceTest extends BaseApiAcceptanceTest {
    @BeforeEach
    void initDateTime() {
        initDateTime(getDateTimeNow());
    }

    private static LocalDateTime getDateTimeNow() {
        return LocalDateTime.parse("2024-03-04T11:00:00");
    }

    @Test
    @DisplayName("""
        T1. Создание сканера сигналов с алгоритмом "Аномальные объемы".
        """)
    void testCase1() {
        integrateInstruments(
            DefaultDataset.imoex(),
            DefaultDataset.sber()
        );

        addSignalScanner(
            AnomalyVolumeScannerRequest.builder()
                .workPeriodInMinutes(1)
                .scaleCoefficient(1.5)
                .description("desc")
                .historyPeriod(180)
                .indexTicker("IMOEX")
                .ids(getInstrumentIds())
                .build()
        );

        assertEquals(1, getSignalScanners().size());
    }

    @Test
    @DisplayName("""
        T2. Создание сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка".
        """)
    void testCase2() {
        integrateInstruments(
            DefaultDataset.sberp(),
            DefaultDataset.sber()
        );

        addSignalScanner(
            PrefSimpleRequest.builder()
                .workPeriodInMinutes(1)
                .ids(getInstrumentIds())
                .description("desc")
                .spreadParam(1.0)
                .build()
        );

        assertEquals(1, getSignalScanners().size());
    }

    @Test
    @DisplayName("""
        T3. Создание сканера сигналов с алгоритмом "Секторальный отстающий".
        """)
    void testCase3() {
        integrateInstruments(
            DefaultDataset.sibn(),
            DefaultDataset.lkoh(),
            DefaultDataset.tatn(),
            DefaultDataset.rosn()
        );

        addSignalScanner(
            SectoralRetardScannerRequest.builder()
                .workPeriodInMinutes(1)
                .ids(getInstrumentIds())
                .description("desc")
                .historyScale(0.015)
                .intradayScale(0.015)
                .build()
        );

        assertEquals(1, getSignalScanners().size());
    }

    @Test
    @DisplayName("""
        T4. Создание сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора".
        """)
    void testCase4() {
        integrateInstruments(
            DefaultDataset.sibn(),
            DefaultDataset.lkoh(),
            DefaultDataset.tatn(),
            DefaultDataset.rosn(),
            DefaultDataset.brf4()
        );

        addSignalScanner(
            CorrelationSectoralScannerRequest.builder()
                .workPeriodInMinutes(1)
                .ids(getInstrumentIds())
                .description("desc")
                .futuresTicker("BRF4")
                .futuresOvernightScale(0.015)
                .stockOvernightScale(0.015)
                .build()
        );

        assertEquals(1, getSignalScanners().size());
    }

    @Test
    @DisplayName("""
        T5. Запуск сканера сигналов с алгоритмом "Аномальные объемы", в торговых данных есть сигнал к покупке.
        """)
    void testCase5() {
        LocalDateTime now = getDateTimeNow();
        LocalDate startDate = now.toLocalDate().minusMonths(1);
        integrateInstruments(
            DefaultDataset.imoex(),
            DefaultDataset.tgkn()
        );
        enableUpdateInstrumentBy(getInstrumentIds());
        datasetRepository().initDailyResultValue(
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
        );
        datasetRepository().initIntradayValue(
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
        );
        addSignalScanner(
            AnomalyVolumeScannerRequest.builder()
                .workPeriodInMinutes(1)
                .scaleCoefficient(1.5)
                .description("desc")
                .historyPeriod(180)
                .indexTicker("IMOEX")
                .ids(getInstrumentIds())
                .build()
        );

        integrateTradingData();

        assertEquals(1, getSignalsBy(getSignalScanners().get(0).getId()).size());
    }

    @Test
    @DisplayName("""
        T6. Запуск сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка", в торговых данных есть сигнал к покупке.
        """)
    void testCase6() {
        LocalDateTime now = getDateTimeNow();
        LocalDate startDate = now.toLocalDate().minusMonths(1);
        integrateInstruments(
            DefaultDataset.sber(),
            DefaultDataset.sberp()
        );
        enableUpdateInstrumentBy(getInstrumentIds());
        datasetRepository().initDailyResultValue(
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
        );
        datasetRepository().initIntradayValue(
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
        );
        addSignalScanner(
            PrefSimpleRequest.builder()
                .workPeriodInMinutes(1)
                .spreadParam(1D)
                .description("desc")
                .ids(getInstrumentIds())
                .build()
        );

        integrateTradingData();

        assertEquals(1, getSignalsBy(getSignalScanners().get(0).getId()).size());
    }

    @Test
    @DisplayName("""
        T7. Запуск сканера сигналов с алгоритмом "Секторальный отстающий", в торговых данных есть сигнал к покупке.
        """)
    void testCase7() {
        LocalDateTime now = getDateTimeNow();
        integrateInstruments(
            DefaultDataset.sibn(),
            DefaultDataset.lkoh(),
            DefaultDataset.tatn(),
            DefaultDataset.rosn()
        );
        enableUpdateInstrumentBy(getInstrumentIds());
        List<HistoryValue> dailyResultValues = new ArrayList<>();
        dailyResultValues.addAll(generator().generateHistory(HistoryGeneratorConfig
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
        dailyResultValues.addAll(generator().generateHistory(HistoryGeneratorConfig
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
        dailyResultValues.addAll(generator().generateHistory(HistoryGeneratorConfig
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
        dailyResultValues.addAll(generator().generateHistory(HistoryGeneratorConfig
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
        datasetRepository().initDailyResultValue(dailyResultValues);
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
        datasetRepository().initIntradayValue(intradayValues);
        addSignalScanner(
            SectoralRetardScannerRequest.builder()
                .workPeriodInMinutes(1)
                .ids(getInstrumentIds())
                .description("desc")
                .historyScale(0.015)
                .intradayScale(0.015)
                .build()
        );

        integrateTradingData();

        assertEquals(1, getSignalsBy(getSignalScanners().get(0).getId()).size());
    }

    @Test
    @DisplayName("""
        T8. Запуск сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора", в торговых данных есть сигнал к покупке.
        """)
    void testCase8() {
        LocalDateTime now = getDateTimeNow();
        integrateInstruments(
            DefaultDataset.sibn(),
            DefaultDataset.brf4()
        );
        enableUpdateInstrumentBy(getInstrumentIds());
        List<HistoryValue> dailyResultValues = new ArrayList<>();
        dailyResultValues.addAll(generator().generateHistory(HistoryGeneratorConfig
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
        dailyResultValues.addAll(generator().generateHistory(HistoryGeneratorConfig
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
        datasetRepository().initDailyResultValue(dailyResultValues);
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
        datasetRepository().initIntradayValue(intradayValues);
        addSignalScanner(
            CorrelationSectoralScannerRequest.builder()
                .workPeriodInMinutes(1)
                .ids(getInstrumentIds())
                .description("desc")
                .futuresTicker("BRF4")
                .futuresOvernightScale(0.015)
                .stockOvernightScale(0.015)
                .build()
        );

        integrateTradingData();

        assertEquals(1, getSignalsBy(getSignalScanners().get(0).getId()).size());
    }
}