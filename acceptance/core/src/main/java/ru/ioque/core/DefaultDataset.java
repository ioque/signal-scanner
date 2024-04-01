package ru.ioque.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.CurrencyPair;
import ru.ioque.core.datagenerator.instrument.Futures;
import ru.ioque.core.datagenerator.instrument.Index;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.instrument.Stock;
import ru.ioque.core.datagenerator.intraday.Contract;
import ru.ioque.core.datagenerator.intraday.Deal;
import ru.ioque.core.datagenerator.intraday.Delta;
import ru.ioque.core.datagenerator.intraday.IntradayValue;
import ru.ioque.core.datagenerator.TradingDataGeneratorFacade;
import ru.ioque.core.datagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.datagenerator.core.ParameterConfig;
import ru.ioque.core.datagenerator.core.PercentageGrowths;
import ru.ioque.core.datagenerator.config.ContractsGeneratorConfig;
import ru.ioque.core.datagenerator.config.DeltasGeneratorConfig;
import ru.ioque.core.datagenerator.config.DealsGeneratorConfig;
import ru.ioque.core.dto.exchange.response.InstrumentInListResponse;
import ru.ioque.core.dto.scanner.request.AddSignalScannerRequest;
import ru.ioque.core.dto.scanner.request.AnomalyVolumeScannerRequest;
import ru.ioque.core.dto.scanner.request.CorrelationSectoralScannerRequest;
import ru.ioque.core.dto.scanner.request.PrefSimpleRequest;
import ru.ioque.core.dto.scanner.request.SectoralRetardScannerRequest;

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

    public static AddSignalScannerRequest getAnomalyVolumeSignalRequest(List<InstrumentInListResponse> instruments) {
        return AnomalyVolumeScannerRequest.builder()
            .workPeriodInMinutes(1)
            .scaleCoefficient(1.5)
            .description("Сканер сигналов с алгоритмом \"Аномальные объемы\": TGKN, TGKB, индекс IMOEX.")
            .historyPeriod(180)
            .indexTicker("IMOEX")
            .ids(instruments
                .stream()
                .filter(row -> List.of("TGKN", "TGKB", "IMOEX").contains(row.getTicker()))
                .map(InstrumentInListResponse::getId)
                .toList())
            .build();
    }

    public static AddSignalScannerRequest getPrefSimpleRequest(List<InstrumentInListResponse> instruments) {
        return PrefSimpleRequest.builder()
            .workPeriodInMinutes(1)
            .ids(instruments
                .stream()
                .filter(row -> List.of("SBER", "SBERP").contains(row.getTicker()))
                .map(InstrumentInListResponse::getId)
                .toList())
            .description("Сканер сигналов с алгоритмом \"Дельта-анализ пар преф-обычка\": SBERP-SBER.")
            .spreadParam(1.0)
            .build();
    }

    public static AddSignalScannerRequest getSectoralRetardScannerRequest(List<InstrumentInListResponse> instruments) {
        return SectoralRetardScannerRequest.builder()
            .workPeriodInMinutes(60)
            .ids(instruments
                .stream()
                .filter(row -> List.of("TATN", "ROSN", "SIBN", "LKOH").contains(row.getTicker()))
                .map(InstrumentInListResponse::getId)
                .toList())
            .description("Сканер сигналов с алгоритмом \"Секторальный отстающий\": TATN, ROSN, SIBN, LKOH.")
            .historyScale(0.015)
            .intradayScale(0.015)
            .build();
    }

    public static AddSignalScannerRequest getCorrelationSectoralScannerRequest(List<InstrumentInListResponse> instruments) {
        return CorrelationSectoralScannerRequest.builder()
            .workPeriodInMinutes(24 * 60)
            .ids(instruments
                .stream()
                .filter(row -> List.of("TATN", "ROSN", "SIBN", "LKOH", "BRF4").contains(row.getTicker()))
                .map(InstrumentInListResponse::getId)
                .toList())
            .description("Сканер сигналов с алгоритмом \"Корреляция сектора с фьючерсом на базовый товар сектора\": TATN, ROSN, SIBN, LKOH, фьючерс BRF4.")
            .futuresTicker("BRF4")
            .futuresOvernightScale(0.015)
            .stockOvernightScale(0.015)
            .build();
    }

    public static List<Instrument> getInstruments() {
        return new ArrayList<>(
            List.of(
                sber(),
                sberp(),
                tatn(),
                tgkn(),
                lkoh(),
                rosn(),
                sibn(),
                usbRub(),
                imoex(),
                brf4()
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

    private static List<Delta> getIndexIntradayValues() {
        return generator.generateDeltas(
            DeltasGeneratorConfig
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

    private static List<Contract> getFuturesIntradayValues() {
        return generator.generateContracts(ContractsGeneratorConfig
            .builder()
            .ticker("BRF4")
            .numTrades(1000)
            .startPrice(120.)
            .date(getLastWorkDay())
            .startTime(LocalTime.parse("10:00"))
            .pricePercentageGrowths(List.of(new PercentageGrowths(10D, 1D)))
            .build());
    }

    private static List<Deal> getStockIntradayValues() {
        return Stream.of("TGKN", "TATN", "ROSN", "SIBN", "LKOH", "SBER", "SBERP")
            .map(row -> generator.generateDeals(defaultStockIntradayConfigBuilder(row)))
            .flatMap(Collection::stream)
            .toList();
    }

    private static List<Deal> getCurrencyPairIntradayValues() {
        return generator.generateDeals(
            DealsGeneratorConfig.builder()
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

    public static List<HistoryValue> getHistoryValues() {
        List<HistoryValue> dailyResults = new ArrayList<>();
        dailyResults.addAll(getStockDailyResults());
        dailyResults.addAll(getFuturesDailyResults());
        dailyResults.addAll(getIndexDailyResults());
        dailyResults.addAll(getCurrencyPairDailyResults());
        return dailyResults;
    }

    private static List<HistoryValue> getCurrencyPairDailyResults() {
        return generator
            .generateHistory(
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

    private static List<HistoryValue> getIndexDailyResults() {
        return generator
            .generateHistory(
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

    private static List<HistoryValue> getFuturesDailyResults() {
        return generator
            .generateHistory(
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

    private static List<HistoryValue> getStockDailyResults() {
        return Stream.of("TGKN", "TATN", "ROSN", "SIBN", "LKOH", "SBER", "SBERP")
            .map(row -> generator.generateHistory(defaultStockHistoryConfig(row)))
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

    private static DealsGeneratorConfig defaultStockIntradayConfigBuilder(String ticker) {
        StartParameters startParameters = getStartParametersBy(ticker);
        return DealsGeneratorConfig.builder()
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

    public static Index imoex() {
        return Index.builder()
            .ticker("IMOEX")
            .name("Индекс фондового рынка мосбиржи")
            .shortName("Индекс фондового рынка мосбиржи")
            .annualHigh(3287.34)
            .annualLow(2126.4)
            .build();
    }

    public static CurrencyPair usbRub() {
        return CurrencyPair.builder()
            .ticker("USD000UTSTOM")
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .lotSize(1000)
            .faceUnit("RUB")
            .build();
    }

    public static Futures brf4() {
        return Futures.builder()
            .ticker("BRF4")
            .name("Фьючерсный контракт BR-1.24")
            .shortName("BR-1.24")
            .lotVolume(1000)
            .assetCode("BR")
            .build();
    }

    public static Stock sber() {
        return Stock.builder()
            .ticker("SBER")
            .lotSize(100)
            .name("ПАО Сбербанк")
            .shortName("Сбербанк")
            .build();
    }

    public static Stock sberp() {
        return Stock.builder()
            .ticker("SBERP")
            .lotSize(100)
            .name("Сбербанк-п")
            .shortName("Сбербанк-п")
            .build();
    }

    public static Stock sibn() {
        return Stock.builder()
            .ticker("SIBN")
            .lotSize(100)
            .name("Газпромнефть")
            .shortName("Газпромнефть")
            .build();
    }

    public static Stock lkoh() {
        return Stock.builder()
            .ticker("LKOH")
            .lotSize(100)
            .name("Лукойл")
            .shortName("Лукойл")
            .build();
    }

    public static Stock rosn() {
        return Stock.builder()
            .ticker("ROSN")
            .lotSize(100)
            .name("Роснефть")
            .shortName("Роснефть")
            .build();
    }

    public static Stock tatn() {
        return Stock.builder()
            .ticker("TATN")
            .lotSize(100)
            .name("Татнефть")
            .shortName("Татнефть")
            .build();
    }

    public static Stock tgkn() {
        return Stock.builder()
            .ticker("TGKN")
            .lotSize(100)
            .name("ТГК-14")
            .shortName("ТГК-14")
            .build();
    }
}
