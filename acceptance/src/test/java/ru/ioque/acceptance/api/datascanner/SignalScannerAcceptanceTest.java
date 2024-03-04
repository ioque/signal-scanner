package ru.ioque.acceptance.api.datascanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.adapters.client.signalscanner.request.AnomalyVolumeScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.CorrelationSectoralScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.PrefSimpleRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.SectoralRetardScannerRequest;
import ru.ioque.acceptance.api.BaseApiAcceptanceTest;
import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.application.tradingdatagenerator.futures.FuturesTradesGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.index.IndexDeltasGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockTradesGeneratorConfig;
import ru.ioque.acceptance.domain.dataemulator.core.DailyResultValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntradayValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("МОДУЛЬ \"СКАНЕР ДАННЫХ\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SignalScannerAcceptanceTest extends BaseApiAcceptanceTest {
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
            instruments().imoex().build(),
            instruments().sber().build()
        );

        addSignalScanner(
            AnomalyVolumeScannerRequest.builder()
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
            instruments().sberp().build(),
            instruments().sber().build()
        );

        addSignalScanner(
            PrefSimpleRequest.builder()
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
            instruments().sibn().build(),
            instruments().lkoh().build(),
            instruments().tatn().build(),
            instruments().rosn().build()
        );

        addSignalScanner(
            SectoralRetardScannerRequest.builder()
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
            instruments().sibn().build(),
            instruments().lkoh().build(),
            instruments().tatn().build(),
            instruments().rosn().build(),
            instruments().brf4().build()
        );

        addSignalScanner(
            CorrelationSectoralScannerRequest.builder()
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
        datasetManager().initInstruments(
            List.of(
                instruments().imoex().build(),
                instruments().tgkn().build()
            )
        );
        datasetManager().initDailyResultValue(
            Stream.concat(
                    generator()
                        .generateStockHistory(
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
                        .generateIndexHistory(
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
        datasetManager().initIntradayValue(
            Stream
                .concat(
                    generator().generateIndexDeltas(
                        IndexDeltasGeneratorConfig
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
                    generator().generateStockTrades(
                        StockTradesGeneratorConfig
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
        fullIntegrate();
        addSignalScanner(
            AnomalyVolumeScannerRequest.builder()
                .scaleCoefficient(1.5)
                .description("desc")
                .historyPeriod(180)
                .indexTicker("IMOEX")
                .ids(getInstrumentIds())
                .build()
        );
        runScanning();

        assertEquals(1, getSignalsBy(getSignalScanners().get(0).getId()).size());
    }

    @Test
    @DisplayName("""
        T6. Запуск сканера сигналов с алгоритмом "Дельта анализ пар преф-обычка", в торговых данных есть сигнал к покупке.
        """)
    void testCase6() {
        LocalDateTime now = getDateTimeNow();
        LocalDate startDate = now.toLocalDate().minusMonths(1);
        datasetManager().initInstruments(
            List.of(
                instruments().sber().build(),
                instruments().sberp().build()
            )
        );
        datasetManager().initDailyResultValue(
            Stream.concat(
                    generator()
                        .generateStockHistory(
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
                        .generateStockHistory(
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
        datasetManager().initIntradayValue(
            Stream
                .concat(
                    generator().generateStockTrades(
                        StockTradesGeneratorConfig
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
                    generator().generateStockTrades(
                        StockTradesGeneratorConfig
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
        fullIntegrate();
        addSignalScanner(
            PrefSimpleRequest.builder()
                .spreadParam(1D)
                .description("desc")
                .ids(getInstrumentIds())
                .build()
        );

        runScanning();

        assertEquals(1, getSignalsBy(getSignalScanners().get(0).getId()).size());
    }

    @Test
    @DisplayName("""
        T7. Запуск сканера сигналов с алгоритмом "Секторальный отстающий", в торговых данных есть сигнал к покупке.
        """)
    void testCase7() {
        LocalDateTime now = getDateTimeNow();
        datasetManager().initInstruments(
            List.of(
                instruments().sibn().build(),
                instruments().lkoh().build(),
                instruments().tatn().build(),
                instruments().rosn().build()
            )
        );
        List<DailyResultValue> dailyResultValues = new ArrayList<>();
        dailyResultValues.addAll(generator().generateStockHistory(HistoryGeneratorConfig
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
        dailyResultValues.addAll(generator().generateStockHistory(HistoryGeneratorConfig
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
        dailyResultValues.addAll(generator().generateStockHistory(HistoryGeneratorConfig
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
        dailyResultValues.addAll(generator().generateStockHistory(HistoryGeneratorConfig
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
        datasetManager().initDailyResultValue(dailyResultValues);
        List<IntradayValue> intradayValues = new ArrayList<>();
        intradayValues.addAll(generator().generateStockTrades(StockTradesGeneratorConfig
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
        intradayValues.addAll(generator().generateStockTrades(StockTradesGeneratorConfig
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
        intradayValues.addAll(generator().generateStockTrades(StockTradesGeneratorConfig
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
        intradayValues.addAll(generator().generateStockTrades(StockTradesGeneratorConfig
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
        datasetManager().initIntradayValue(intradayValues);
        fullIntegrate();
        addSignalScanner(
            SectoralRetardScannerRequest.builder()
                .ids(getInstrumentIds())
                .description("desc")
                .historyScale(0.015)
                .intradayScale(0.015)
                .build()
        );

        runScanning();

        assertEquals(1, getSignalsBy(getSignalScanners().get(0).getId()).size());
    }

    @Test
    @DisplayName("""
        T8. Запуск сканера сигналов с алгоритмом "Корреляция сектора с фьючерсом на товар сектора", в торговых данных есть сигнал к покупке.
        """)
    void testCase8() {
        LocalDateTime now = getDateTimeNow();
        datasetManager().initInstruments(
            List.of(
                instruments().sibn().build(),
                instruments().brf4().build()
            )
        );
        List<DailyResultValue> dailyResultValues = new ArrayList<>();
        dailyResultValues.addAll(generator().generateStockHistory(HistoryGeneratorConfig
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
        dailyResultValues.addAll(generator().generateFuturesHistory(HistoryGeneratorConfig
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
        datasetManager().initDailyResultValue(dailyResultValues);
        List<IntradayValue> intradayValues = new ArrayList<>();
        intradayValues.addAll(generator().generateStockTrades(StockTradesGeneratorConfig
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
        intradayValues.addAll(generator().generateFuturesTrades(FuturesTradesGeneratorConfig
            .builder()
            .ticker("BRF4")
            .numTrades(2000)
            .startPrice(120.)
            .date(now.toLocalDate())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
            .build()));
        datasetManager().initIntradayValue(intradayValues);
        fullIntegrate();
        addSignalScanner(
            CorrelationSectoralScannerRequest.builder()
                .ids(getInstrumentIds())
                .description("desc")
                .futuresTicker("BRF4")
                .futuresOvernightScale(0.015)
                .stockOvernightScale(0.015)
                .build()
        );

        runScanning();

        assertEquals(1, getSignalsBy(getSignalScanners().get(0).getId()).size());
    }
}
