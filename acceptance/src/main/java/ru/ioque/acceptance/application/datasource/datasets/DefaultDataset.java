package ru.ioque.acceptance.application.datasource.datasets;

import ru.ioque.acceptance.application.tradingdatagenerator.TradingDataGeneratorFacade;
import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.application.tradingdatagenerator.currencypair.CurrencyPairTradeGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.futures.FuturesTradesGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.index.IndexDeltasGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockTradesGeneratorConfig;
import ru.ioque.acceptance.domain.dataemulator.core.DailyResultValue;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentValue;
import ru.ioque.acceptance.domain.dataemulator.core.IntradayValue;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairDailyResult;
import ru.ioque.acceptance.domain.dataemulator.currencyPair.CurrencyPairTrade;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesDailyResult;
import ru.ioque.acceptance.domain.dataemulator.futures.FuturesTrade;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDailyResult;
import ru.ioque.acceptance.domain.dataemulator.index.IndexDelta;
import ru.ioque.acceptance.domain.dataemulator.stock.StockDailyResult;
import ru.ioque.acceptance.domain.dataemulator.stock.StockTrade;
import ru.ioque.acceptance.fixture.InstrumentsFixture;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class DefaultDataset {
    static TradingDataGeneratorFacade generator = new TradingDataGeneratorFacade();
    static InstrumentsFixture instrumentsFixture = new InstrumentsFixture();

    public static List<InstrumentValue> getInstruments() {
        return new ArrayList<>(
            List.of(
                instrumentsFixture.sber().build(),
                instrumentsFixture.sberp().build(),
                instrumentsFixture.tatn().build(),
                instrumentsFixture.tgkn().build(),
                instrumentsFixture.lkoh().build(),
                instrumentsFixture.rosn().build(),
                instrumentsFixture.sibn().build(),
                instrumentsFixture.usbRub().build(),
                instrumentsFixture.imoex().build(),
                instrumentsFixture.brf4().build()
            )
        );
    }

    public static List<IntradayValue> getIntradayValues() {
        List<IntradayValue> intradayValues = new ArrayList<>();
        intradayValues.addAll(getStockIntradayValues());
        intradayValues.addAll(getFuturesIntradayValues());
        intradayValues.addAll(getIndexIntradayValues());
        intradayValues.addAll(getCurrencyPairIntradayValues());
        return intradayValues;
    }

    private static List<IndexDelta> getIndexIntradayValues() {
        return generator.generateIndexDeltas(
            IndexDeltasGeneratorConfig
                .builder()
                .ticker("IMOEX")
                .numTrades(1000)
                .startPrice(3500.)
                .startValue(200_000D)
                .date(getLastWorkDay())
                .startTime(LocalTime.parse("10:00"))
                .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(30D, 1D)))
                .build()
        );
    }

    private static List<FuturesTrade> getFuturesIntradayValues() {
        return generator.generateFuturesTrades(FuturesTradesGeneratorConfig
            .builder()
            .ticker("BRF4")
            .numTrades(1000)
            .startPrice(120.)
            .date(getLastWorkDay())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
            .build());
    }

    private static List<StockTrade> getStockIntradayValues() {
        return Stream.of("TGKN", "TATN", "ROSN", "SIBN", "LKOH", "SBER", "SBERP")
            .map(row -> generator.generateStockTrades(defaultStockIntradayConfigBuilder().ticker(row).build()))
            .flatMap(Collection::stream)
            .toList();
    }

    private static List<CurrencyPairTrade> getCurrencyPairIntradayValues() {
        return generator.generateCurrencyPairTrades(
            CurrencyPairTradeGeneratorConfig.builder()
                .ticker("USDRUB_TOM")
                .numTrades(1000)
                .startPrice(70)
                .startValue(200_000D)
                .date(getLastWorkDay())
                .startTime(LocalTime.parse("10:00"))
                .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
                .valuePercentageGrowths(List.of(new PercentageGrowths(30D, 1D)))
                .build()
        );
    }

    public static List<? extends DailyResultValue> getDailyResults() {
        List<DailyResultValue> dailyResults = new ArrayList<>();
        dailyResults.addAll(getStockDailyResults());
        dailyResults.addAll(getFuturesDailyResults());
        dailyResults.addAll(getIndexDailyResults());
        dailyResults.addAll(getCurrencyPairDailyResults());
        return dailyResults;
    }

    private static List<CurrencyPairDailyResult> getCurrencyPairDailyResults() {
        return generator
            .generateCurrencyPairHistory(
                HistoryGeneratorConfig
                    .builder()
                    .ticker("USDRUB_TOM")
                    .startClose(60)
                    .startOpen(62.)
                    .startValue(2_000_000D)
                    .days(180)
                    .startDate(getLastWorkDay().minusDays(180))
                    .openPricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .closePricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .valuePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .build()
            );
    }

    private static List<IndexDailyResult> getIndexDailyResults() {
        return generator
            .generateIndexHistory(
                HistoryGeneratorConfig
                    .builder()
                    .ticker("IMOEX")
                    .startClose(2500.)
                    .startOpen(2600.)
                    .startValue(2_000_000D)
                    .days(180)
                    .startDate(getLastWorkDay().minusDays(180))
                    .openPricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .closePricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .valuePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .build()
            );
    }

    private static List<FuturesDailyResult> getFuturesDailyResults() {
        return generator
            .generateFuturesHistory(
                HistoryGeneratorConfig
                    .builder()
                    .ticker("BRF4")
                    .startClose(80.)
                    .startOpen(80)
                    .startValue(2_000_000D)
                    .days(180)
                    .startDate(getLastWorkDay().minusDays(180))
                    .openPricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .closePricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .valuePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                    .build()
            );
    }

    private static List<StockDailyResult> getStockDailyResults() {
        return Stream.of("TGKN", "TATN", "ROSN", "SIBN", "LKOH", "SBER", "SBERP")
            .map(row -> generator.generateStockHistory(defaultStockHistoryConfigBuilder().ticker(row).build()))
            .flatMap(Collection::stream)
            .toList();
    }

    private static HistoryGeneratorConfig.HistoryGeneratorConfigBuilder defaultStockHistoryConfigBuilder() {
        return HistoryGeneratorConfig
            .builder()
            .startClose(100.)
            .startOpen(105.)
            .startValue(2_000_000D)
            .days(180)
            .startDate(getLastWorkDay().minusDays(180))
            .openPricePercentageGrowths(List.of(
                    new PercentageGrowths(-5D, 0.25),
                    new PercentageGrowths(-10D, 0.25),
                    new PercentageGrowths(10D, 0.25),
                    new PercentageGrowths(15D, 0.25)
                )
            )
            .closePricePercentageGrowths(List.of(
                new PercentageGrowths(-5D, 0.25),
                new PercentageGrowths(-10D, 0.25),
                new PercentageGrowths(10D, 0.25),
                new PercentageGrowths(15D, 0.25)
                )
            )
            .valuePercentageGrowths(List.of(
                new PercentageGrowths(-5D, 0.25),
                new PercentageGrowths(-10D, 0.25),
                new PercentageGrowths(10D, 0.25),
                new PercentageGrowths(15D, 0.25)
                )
            );
    }

    private static StockTradesGeneratorConfig.StockTradesGeneratorConfigBuilder defaultStockIntradayConfigBuilder() {
        return StockTradesGeneratorConfig.builder()
            .numTrades(1000)
            .startPrice(100)
            .startValue(200_000D)
            .date(getLastWorkDay())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(
                new PercentageGrowths(-5D, 0.25),
                new PercentageGrowths(5D, 0.25),
                new PercentageGrowths(-10D, 0.25),
                new PercentageGrowths(30D, 0.25)
            ))
            .valuePercentageGrowths(List.of(
                new PercentageGrowths(-5D, 0.5),
                new PercentageGrowths(10D, 0.5)
            ));
    }

    private static LocalDate getLastWorkDay() {
        LocalDate now = LocalDate.now();
        if (now.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            now = now.minusDays(2);
        }
        if (now.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            now = now.minusDays(1);
        }
        return now;
    }
}
