package ru.ioque.acceptance.application.datasource.datasets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.acceptance.adapters.client.signalscanner.request.AddSignalScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.AnomalyVolumeScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.CorrelationSectoralScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.PrefSimpleRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.SectoralRetardScannerRequest;
import ru.ioque.acceptance.application.tradingdatagenerator.TradingDataGeneratorFacade;
import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.ParameterConfig;
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
import ru.ioque.acceptance.domain.exchange.InstrumentInList;
import ru.ioque.acceptance.fixture.InstrumentsFixture;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DefaultDataset {
    static TradingDataGeneratorFacade generator = new TradingDataGeneratorFacade();
    static InstrumentsFixture instrumentsFixture = new InstrumentsFixture();

    public static AddSignalScannerRequest getAnomalyVolumeSignalRequest(List<InstrumentInList> instruments) {
        return AnomalyVolumeScannerRequest.builder()
            .scaleCoefficient(1.5)
            .description("desc")
            .historyPeriod(180)
            .indexTicker("IMOEX")
            .ids(instruments
                .stream()
                .filter(row -> List.of("TGKN", "TGKB", "IMOEX").contains(row.getTicker()))
                .map(InstrumentInList::getId)
                .toList())
            .build();
    }

    public static AddSignalScannerRequest getPrefSimpleRequest(List<InstrumentInList> instruments) {
        return PrefSimpleRequest.builder()
            .ids(instruments
                .stream()
                .filter(row -> List.of("SBER", "SBERP").contains(row.getTicker()))
                .map(InstrumentInList::getId)
                .toList())
            .description("desc")
            .spreadParam(1.0)
            .build();
    }

    public static AddSignalScannerRequest getSectoralRetardScannerRequest(List<InstrumentInList> instruments) {
        return SectoralRetardScannerRequest.builder()
            .ids(instruments
                .stream()
                .filter(row -> List.of("TATN", "ROSN", "SIBN", "LKOH").contains(row.getTicker()))
                .map(InstrumentInList::getId)
                .toList())
            .description("desc")
            .historyScale(0.015)
            .intradayScale(0.015)
            .build();
    }

    public static AddSignalScannerRequest getCorrelationSectoralScannerRequest(List<InstrumentInList> instruments) {
        return CorrelationSectoralScannerRequest.builder()
            .ids(instruments
                .stream()
                .filter(row -> List.of("TATN", "ROSN", "SIBN", "LKOH", "BRF4").contains(row.getTicker()))
                .map(InstrumentInList::getId)
                .toList())
            .description("desc")
            .futuresTicker("BRF4")
            .futuresOvernightScale(0.015)
            .stockOvernightScale(0.015)
            .build();
    }

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
            .map(row -> generator.generateStockTrades(defaultStockIntradayConfigBuilder(row)))
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
            .map(row -> generator.generateStockHistory(defaultStockHistoryConfig(row)))
            .flatMap(Collection::stream)
            .toList();
    }

    private static HistoryGeneratorConfig defaultStockHistoryConfig(String ticker) {
        StartParameters startParameters = getStartParametersBy(ticker);
        return HistoryGeneratorConfig
            .builder()
            .ticker(ticker)
            .startClose(startParameters.getClosePrice().getStartValue())
            .startOpen(startParameters.getOpenPrice().getStartValue())
            .startValue(startParameters.getHistoryValue().getStartValue())
            .days(180)
            .startDate(getLastWorkDay().minusDays(180))
            .openPricePercentageGrowths(startParameters.getOpenPrice().getPercentageGrowths())
            .closePricePercentageGrowths(startParameters.getClosePrice().getPercentageGrowths())
            .valuePercentageGrowths(startParameters.getHistoryValue().getPercentageGrowths())
            .build();
    }

    private static StockTradesGeneratorConfig defaultStockIntradayConfigBuilder(String ticker) {
        StartParameters startParameters = getStartParametersBy(ticker);
        return StockTradesGeneratorConfig.builder()
            .ticker(ticker)
            .numTrades(1000)
            .startPrice(startParameters.getIntradayPrice().getStartValue())
            .startValue(startParameters.getIntradayValue().getStartValue())
            .date(getLastWorkDay())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(startParameters.getIntradayPrice().getPercentageGrowths())
            .valuePercentageGrowths(startParameters.getIntradayValue().getPercentageGrowths())
            .build();
    }

    public static LocalDate getLastWorkDay() {
        LocalDate now = LocalDate.now();
        if (now.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            now = now.minusDays(2);
        }
        if (now.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            now = now.minusDays(1);
        }
        return now;
    }

    private static StartParameters getStartParametersBy(String ticker) {
        return startParametersMap.get(ticker);
    }

    private static final Map<String, StartParameters> startParametersMap = Map.of(
        "TGKN", new StartParameters(
            new ParameterConfig("openPrice", 70D, neutralHistoryTrend()),
            new ParameterConfig("closePrice", 72D, neutralHistoryTrend()),
            new ParameterConfig("intradayPrice", 72D, upDailyTrend()),
            new ParameterConfig("historyValue", 700_000D, neutralHistoryTrend()),
            new ParameterConfig("intradayValue", 700_000D, upValueDailyTrend())
        ),
        "TATN",  new StartParameters(
            new ParameterConfig("openPrice", 210D, upHistoryTrend()),
            new ParameterConfig("closePrice", 212D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 240D, upDailyTrend()),
            new ParameterConfig("historyValue", 7_000_000D, upHistoryTrend()),
            new ParameterConfig("intradayValue", 70D, upValueDailyTrend())
        ),
        "ROSN",  new StartParameters(
            new ParameterConfig("openPrice", 340D, upHistoryTrend()),
            new ParameterConfig("closePrice", 345D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 390D, upDailyTrend()),
            new ParameterConfig("historyValue", 700_000D, upHistoryTrend()),
            new ParameterConfig("intradayValue", 50_000D, upValueDailyTrend())
        ),
        "SIBN",  new StartParameters(
            new ParameterConfig("openPrice", 120D, upHistoryTrend()),
            new ParameterConfig("closePrice", 122D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 130D, downDailyTrend()),
            new ParameterConfig("historyValue", 5_000_000D, neutralHistoryTrend()),
            new ParameterConfig("intradayValue", 100_000D, upValueDailyTrend())
        ),
        "LKOH",  new StartParameters(
            new ParameterConfig("openPrice", 4000D, upHistoryTrend()),
            new ParameterConfig("closePrice", 4123D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 4500D, upDailyTrend()),
            new ParameterConfig("historyValue", 7_000_000D, upHistoryTrend()),
            new ParameterConfig("intradayValue", 200_000D, upValueDailyTrend())
        ),
        "SBER",  new StartParameters(
            new ParameterConfig("openPrice", 240D, upHistoryTrend()),
            new ParameterConfig("closePrice", 242D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 270D, upDailyTrend()),
            new ParameterConfig("historyValue", 700_000D, upHistoryTrend()),
            new ParameterConfig("intradayValue", 1_000_000D, upValueDailyTrend())
        ),
        "SBERP", new StartParameters(
            new ParameterConfig("openPrice", 234D, upHistoryTrend()),
            new ParameterConfig("closePrice", 238D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 267D, downDailyTrend()),
            new ParameterConfig("historyValue", 700_000D, upHistoryTrend()),
            new ParameterConfig("intradayValue", 1_000_000D, upValueDailyTrend())
        ),
        "IMOEX", new StartParameters(
            new ParameterConfig("openPrice", 3000D, upHistoryTrend()),
            new ParameterConfig("closePrice", 3000D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 3300D, upDailyTrend()),
            new ParameterConfig("historyValue", 70_00_000D, neutralHistoryTrend()),
            new ParameterConfig("intradayValue", 1_000_000D, upValueDailyTrend())
        ),
        "BRF4", new StartParameters(
            new ParameterConfig("openPrice", 70D, upHistoryTrend()),
            new ParameterConfig("closePrice", 72D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 80D, upDailyTrend()),
            new ParameterConfig("historyValue", 700_000D, neutralHistoryTrend()),
            new ParameterConfig("intradayValue", 1_000_000D, upValueDailyTrend())
        ),
        "USDRUB_TOM", new StartParameters(
            new ParameterConfig("openPrice", 70D, upHistoryTrend()),
            new ParameterConfig("closePrice", 72D, upHistoryTrend()),
            new ParameterConfig("intradayPrice", 72D, upDailyTrend()),
            new ParameterConfig("historyValue", 700_000D, neutralHistoryTrend()),
            new ParameterConfig("intradayValue", 1_000_000D, upValueDailyTrend())
        )
    );

    private static List<PercentageGrowths> upDailyTrend() {
        return List.of(
            new PercentageGrowths(5D, 0.25),
            new PercentageGrowths(-2D, 0.25),
            new PercentageGrowths(-1D, 0.25),
            new PercentageGrowths(15D, 0.25)
        );
    }

    private static List<PercentageGrowths> downDailyTrend() {
        return List.of(
            new PercentageGrowths(5D, 0.25),
            new PercentageGrowths(-10D, 0.25),
            new PercentageGrowths(2D, 0.25),
            new PercentageGrowths(-15D, 0.25)
        );
    }

    private static List<PercentageGrowths> upValueDailyTrend() {
        return List.of(
            new PercentageGrowths(-5D, 0.5),
            new PercentageGrowths(10D, 0.5)
        );
    }

    private static List<PercentageGrowths> upHistoryTrend() {
        return List.of(
            new PercentageGrowths(-5D, 0.25),
            new PercentageGrowths(5D, 0.25),
            new PercentageGrowths(-10D, 0.25),
            new PercentageGrowths(30D, 0.25)
        );
    }

    private static List<PercentageGrowths> neutralHistoryTrend() {
        return List.of(
            new PercentageGrowths(-5D, 0.25),
            new PercentageGrowths(5D, 0.25),
            new PercentageGrowths(-5D, 0.25),
            new PercentageGrowths(5D, 0.25)
        );
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private static class StartParameters {
        ParameterConfig openPrice;
        ParameterConfig closePrice;
        ParameterConfig intradayPrice;
        ParameterConfig historyValue;
        ParameterConfig intradayValue;
    }
}
