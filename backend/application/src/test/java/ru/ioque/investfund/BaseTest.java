package ru.ioque.investfund;

import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.AddScannerCommand;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.Deal;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.exchange.value.FuturesDeal;
import ru.ioque.investfund.domain.exchange.value.FuturesDealResult;
import ru.ioque.investfund.domain.exchange.value.IndexDelta;
import ru.ioque.investfund.domain.exchange.value.IndexDeltaResult;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;
import ru.ioque.investfund.domain.scanner.entity.SignalConfig;
import ru.ioque.investfund.fakes.FakeDIContainer;
import ru.ioque.investfund.fakes.FakeDateTimeProvider;
import ru.ioque.investfund.fakes.FakeEventBus;
import ru.ioque.investfund.fakes.FakeExchangeRepository;
import ru.ioque.investfund.fakes.FakeLoggerProvider;
import ru.ioque.investfund.fakes.FakeScannerLogRepository;
import ru.ioque.investfund.fakes.FakeScannerRepository;
import ru.ioque.investfund.fixture.ExchangeDataFixture;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class BaseTest {
    private final FakeDIContainer fakeDIContainer = new FakeDIContainer();

    protected ExchangeDataFixture exchangeDataFixture() {
        return fakeDIContainer.getExchangeDataFixture();
    }

    protected FakeExchangeRepository exchangeRepository() {
        return (FakeExchangeRepository) fakeDIContainer.getExchangeRepository();
    }

    protected FakeDateTimeProvider dateTimeProvider() {
        return (FakeDateTimeProvider) fakeDIContainer.getDateTimeProvider();
    }

    protected FakeScannerRepository signalProducerRepo() {
        return (FakeScannerRepository) fakeDIContainer.getScannerRepository();
    }

    protected final ScannerManager dataScannerManager() {
        return fakeDIContainer.getScannerManager();
    }

    protected final FakeScannerLogRepository scannerLogRepository() {
        return (FakeScannerLogRepository) fakeDIContainer.getScannerLogRepository();
    }

    protected final FakeLoggerProvider loggerProvider() {
        return (FakeLoggerProvider) fakeDIContainer.getLoggerProvider();
    }

    protected final ExchangeManager exchangeManager() {
        return fakeDIContainer.getExchangeManager();
    }

    protected final FakeEventBus eventBus() {
        return (FakeEventBus) fakeDIContainer.getEventBus();
    }

    protected final FakeScannerRepository fakeDataScannerStorage() {
        return (FakeScannerRepository) fakeDIContainer.getScannerRepository();
    }

    protected LocalDate nowMinus1Days() {
        return dateTimeProvider().nowDateTime().toLocalDate().minusDays(1);
    }

    protected LocalDate nowMinus3Month() {
        return dateTimeProvider().nowDateTime().minusMonths(3).toLocalDate();
    }


    protected void addScanner(
        SignalConfig config
    ) {
        dataScannerManager()
            .saveConfiguration(
                AddScannerCommand.builder()
                    .signalConfig(config)
                    .build()
            );
    }

    protected List<Instrument> getInstruments() {
        return exchangeRepository().getBy(dateTimeProvider().nowDate()).map(Exchange::getInstruments).orElse(new ArrayList<>());
    }

    protected void initTodayDateTime(String text) {
        dateTimeProvider().setNow(LocalDateTime.parse(text));
    }

    protected Stream<Instrument> getInstrumentsBy(List<String> tickers) {
        return getInstruments().stream().filter(row -> tickers.contains(row.getTicker()));
    }

    protected List<DailyValue> generateTradingResultsBy(String ticker, LocalDate start, LocalDate stop) {
        final List<DailyValue> dailyValues = new ArrayList<>();
        var cursor = start;
        while (cursor.isBefore(stop) || cursor.isEqual(stop)) {
            if (!cursor.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !cursor.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                dailyValues.add(buildTradingResultWith(ticker, cursor).build());
            }
            cursor = cursor.plusDays(1);
        }
        return dailyValues;
    }

    protected Instrument getInstrumentBy(String afks) {
        return getInstruments().stream().filter(row -> row.getTicker().equals(afks)).findFirst().orElseThrow();
    }

    protected void integrateInstruments(Instrument... instruments) {
        exchangeDataFixture().initInstruments(Arrays.asList(instruments));
        exchangeManager().integrateWithDataSource();
    }

    protected void initInstruments(Instrument... instruments) {
        exchangeDataFixture().initInstruments(Arrays.asList(instruments));
    }

    protected void initTradingResults(List<DailyValue> tradingResults) {
        exchangeDataFixture().initTradingResults(tradingResults);
    }

    protected void initTradingResults(DailyValue... tradingResults) {
        initTradingResults(Arrays.asList(tradingResults));
    }

    protected void initDealDatas(IntradayValue... intradayValues) {
        exchangeDataFixture().initDealDatas(Arrays.asList(intradayValues));
    }


    protected List<IntradayValue> getIntradayValue(String ticker) {
        return getInstrumentBy(ticker).getIntradayValues().stream().toList();
    }

    protected List<DailyValue> getDailyTradingResultsBy(String ticker) {
        return getInstrumentBy(ticker).getDailyValues().stream().toList();
    }

    protected void clearLogs() {
        loggerProvider().clearLogs();
    }

    protected List<UUID> getInstrumentIds() {
        return getInstruments().stream().map(Instrument::getId).toList();
    }

    protected Deal buildBuyDealBy(Long number, String ticker, String localTime, Double price, Double value, Integer qnt) {
        return Deal.builder()
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .value(value)
            .isBuy(true)
            .qnt(qnt)
            .price(price)
            .number(number)
            .build();
    }

    protected Deal buildSellDealBy(Long number, String ticker, String localTime, Double price, Double value, Integer qnt) {
        return Deal.builder()
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .value(value)
            .isBuy(false)
            .qnt(qnt)
            .price(price)
            .number(number)
            .build();
    }

    protected FuturesDeal buildFuturesDealBy(Long number, String ticker, String localTime, Double price, Double value, Integer qnt) {
        return FuturesDeal.builder()
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .price(price)
            .number(number)
            .qnt(qnt)
            .value(value)
            .build();
    }

    protected IndexDelta buildDeltaBy(Long number, String ticker, String localTime, Double price, Double value) {
        return IndexDelta.builder()
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .value(value)
            .price(price)
            .number(number)
            .build();
    }

    protected DailyValue buildFuturesDealResultBy(String ticker, String tradeDate, Double openPrice, Double closePrice, Double value, Integer volume) {
        return FuturesDealResult.builder()
            .ticker(ticker)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(1D)
            .closePrice(closePrice)
            .maxPrice(1D)
            .minPrice(1D)
            .value(value)
            .volume(volume)
            .openPositionValue(1D)
            .build();
    }

    protected DailyValue buildDeltaResultBy(String ticker, String tradeDate, double openPrice, double closePrice, double value) {
        return IndexDeltaResult.builder()
            .ticker(ticker)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .maxPrice(1D)
            .minPrice(1D)
            .value(value)
            .capitalization(1D)
            .build();
    }

    protected DealResult buildDealResultBy(String ticker, String tradeDate, Double openPrice, Double closePrice, Double waPrice, Double value) {
        return DealResult.builder()
            .ticker(ticker)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .numTrades(1D)
            .maxPrice(1D)
            .minPrice(1D)
            .value(value)
            .waPrice(waPrice)
            .build();
    }

    protected Index imoex() {
        return Index
            .builder()
            .id(UUID.randomUUID())
            .ticker("IMOEX")
            .name("Индекс МосБиржи")
            .shortName("Индекс МосБиржи")
            .annualLow(100D)
            .annualHigh(100D)
            .build();
    }

    protected DealResult.DealResultBuilder buildTradingResultWith(String ticker, LocalDate localDate) {
        return DealResult.builder()
            .tradeDate(localDate)
            .ticker(ticker)
            .openPrice(1.0)
            .closePrice(1.0)
            .minPrice(1.0)
            .maxPrice(1.0)
            .value(1.0)
            .numTrades(1D)
            .volume(1D)
            .waPrice(1D);
    }

    protected Deal buildDealWith(String ticker, LocalDateTime dateTime) {
        return Deal.builder()
            .dateTime(dateTime)
            .ticker(ticker)
            .value(10000.0)
            .isBuy(false)
            .qnt(1)
            .price(100D)
            .number(1L)
            .build();
    }

    protected Stock afks() {
        return Stock
            .builder()
            .id(fakeDIContainer.getUuidProvider().generate())
            .shortName("ао Система")
            .name("АФК Система")
            .ticker("AFKS")
            .lotSize(10000)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Stock sberP() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .ticker("SBERP")
            .shortName("Сбер п")
            .name("Сбербанк П")
            .lotSize(100)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Stock sber() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .ticker("SBER")
            .shortName("Сбер")
            .name("Сбербанк")
            .lotSize(100)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Stock sibn() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .ticker("SIBN")
            .shortName("Газпромнефть")
            .name("Газпромнефть")
            .lotSize(100)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Futures brf4() {
        return Futures
            .builder()
            .id(UUID.randomUUID())
            .ticker("BRF4")
            .name("Фьючерсный контракт BR-1.24")
            .shortName("BR-1.24")
            .assetCode("BR")
            .lowLimit(100D)
            .highLimit(100D)
            .initialMargin(100D)
            .lotVolume(1000)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Stock lkoh() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .ticker("LKOH")
            .shortName("Лукойл")
            .name("Лукойл")
            .lotSize(100)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Stock tatn() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .ticker("TATN")
            .shortName("Татнефть")
            .name("Татнефть")
            .lotSize(100)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Stock rosn() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .ticker("ROSN")
            .shortName("Роснефть")
            .name("Роснефть")
            .lotSize(100)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected CurrencyPair usdRub() {
        return CurrencyPair
            .builder()
            .id(UUID.randomUUID())
            .ticker("USD000UTSTOM")
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .faceUnit("RUB")
            .lotSize(1000)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Stock tgkb() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .name("TGKB")
            .ticker("TGKB")
            .shortName("TGKB")
            .lotSize(100)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

    protected Stock tgkn() {
        return Stock
            .builder()
            .id(UUID.randomUUID())
            .name("TGKN")
            .ticker("TGKN")
            .shortName("TGKN")
            .lotSize(100)
            .intradayValues(new ArrayList<>())
            .dailyValues(new ArrayList<>())
            .build();
    }

}
