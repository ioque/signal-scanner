package ru.ioque.investfund;

import ru.ioque.investfund.application.modules.configurator.ScannerConfigurator;
import ru.ioque.investfund.application.modules.datasource.DatasourceManager;
import ru.ioque.investfund.application.modules.scanner.AddScannerCommand;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.application.modules.scanner.UpdateScannerCommand;
import ru.ioque.investfund.domain.configurator.AlgorithmConfig;
import ru.ioque.investfund.domain.datasource.entity.CurrencyPair;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Futures;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.Stock;
import ru.ioque.investfund.domain.datasource.value.Contract;
import ru.ioque.investfund.domain.datasource.value.Deal;
import ru.ioque.investfund.domain.datasource.value.Delta;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.fakes.FakeDIContainer;
import ru.ioque.investfund.fakes.FakeDatasourceRepository;
import ru.ioque.investfund.fakes.FakeDateTimeProvider;
import ru.ioque.investfund.fakes.FakeEventBus;
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
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class BaseTest {
    private final FakeDIContainer fakeDIContainer = new FakeDIContainer();

    protected ExchangeDataFixture exchangeDataFixture() {
        return fakeDIContainer.getExchangeDataFixture();
    }

    protected FakeDatasourceRepository exchangeRepository() {
        return fakeDIContainer.getDatasourceRepository();
    }

    protected FakeDateTimeProvider dateTimeProvider() {
        return fakeDIContainer.getDateTimeProvider();
    }

    protected FakeScannerRepository signalProducerRepo() {
        return fakeDIContainer.getScannerRepository();
    }

    protected final ScannerManager dataScannerManager() {
        return fakeDIContainer.getScannerManager();
    }

    protected final FakeScannerLogRepository scannerLogRepository() {
        return fakeDIContainer.getScannerLogRepository();
    }

    protected final FakeLoggerProvider loggerProvider() {
        return fakeDIContainer.getLoggerProvider();
    }

    protected final DatasourceManager exchangeManager() {
        return fakeDIContainer.getDatasourceManager();
    }

    protected final ScannerConfigurator scannerConfigurator() {
        return fakeDIContainer.getScannerConfigurator();
    }

    protected final FakeEventBus eventBus() {
        return fakeDIContainer.getEventBus();
    }

    protected final FakeScannerRepository fakeDataScannerStorage() {
        return fakeDIContainer.getScannerRepository();
    }

    protected LocalDate nowMinus1Days() {
        return dateTimeProvider().nowDateTime().toLocalDate().minusDays(1);
    }

    protected LocalDate nowMinus3Month() {
        return dateTimeProvider().nowDateTime().minusMonths(3).toLocalDate();
    }

    protected void addScanner(
        Integer workPeriodInMinutes,
        String description,
        List<String> tickers,
        AlgorithmConfig config
    ) {
        scannerConfigurator()
            .addNewScanner(
                AddScannerCommand.builder()
                    .workPeriodInMinutes(workPeriodInMinutes)
                    .description(description)
                    .tickers(tickers)
                    .algorithmConfig(config)
                    .build()
            );
    }

    protected void updateScanner(UpdateScannerCommand command) {
        scannerConfigurator().updateScanner(command);
    }

    protected List<Instrument> getInstruments(UUID datasourceId) {
        return exchangeRepository()
            .getBy(datasourceId)
            .map(Datasource::getInstruments)
            .orElse(new ArrayList<>());
    }

    protected void initTodayDateTime(String dateTime) {
        dateTimeProvider().setNow(LocalDateTime.parse(dateTime));
    }

    protected Stream<Instrument> getInstrumentsBy(UUID datasourceId, List<String> tickers) {
        return getInstruments(datasourceId).stream().filter(row -> tickers.contains(row.getTicker()));
    }

    protected List<HistoryValue> generateTradingResultsBy(String ticker, LocalDate start, LocalDate stop) {
        final List<HistoryValue> historyValues = new ArrayList<>();
        var cursor = start;
        while (cursor.isBefore(stop) || cursor.isEqual(stop)) {
            if (!cursor.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !cursor.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                historyValues.add(buildTradingResultWith(ticker, cursor).build());
            }
            cursor = cursor.plusDays(1);
        }
        return historyValues;
    }

    protected void integrateInstruments(UUID datasourceId, Instrument... instruments) {
        exchangeDataFixture().initInstruments(Arrays.asList(instruments));
        exchangeManager().integrateInstruments(datasourceId);
    }

    protected void initInstruments(Instrument... instruments) {
        exchangeDataFixture().initInstruments(Arrays.asList(instruments));
    }

    protected void initTradingResults(List<HistoryValue> tradingResults) {
        exchangeDataFixture().initTradingResults(tradingResults);
    }

    protected void initTradingResults(HistoryValue... tradingResults) {
        initTradingResults(Arrays.asList(tradingResults));
    }

    protected void initDealDatas(IntradayValue... intradayValues) {
        exchangeDataFixture().initDealDatas(Arrays.asList(intradayValues));
    }

    protected List<IntradayValue> getIntradayValuesBy(String ticker) {
        return exchangeRepository().getIntradayValues().get(ticker);
    }

    protected List<IntradayValue> getIntradayValues() {
        return exchangeRepository().getIntradayValues().values().stream().flatMap(Collection::stream).toList();
    }

    protected List<HistoryValue> getHistoryValuesBy(String ticker) {
        return exchangeRepository().getHistoryValues().get(ticker);
    }

    protected List<HistoryValue> getHistoryValues() {
        return exchangeRepository().getHistoryValues().values().stream().flatMap(Collection::stream).toList();
    }

    protected void clearLogs() {
        loggerProvider().clearLogs();
    }

    protected List<String> getTickers(UUID datasourceId) {
        return getInstruments(datasourceId).stream().map(Instrument::getTicker).toList();
    }

    protected Deal buildBuyDealBy(
        Long number,
        String ticker,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Deal.builder()
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .value(value)
            .isBuy(true)
            .qnt(qnt)
            .price(price)
            .build();
    }

    protected Deal buildSellDealBy(
        Long number,
        String ticker,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Deal.builder()
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .value(value)
            .isBuy(false)
            .qnt(qnt)
            .price(price)
            .build();
    }

    protected Contract buildContractBy(
        Long number,
        String ticker,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Contract.builder()
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .price(price)
            .qnt(qnt)
            .value(value)
            .build();
    }

    protected Delta buildDeltaBy(Long number, String ticker, String localTime, Double price, Double value) {
        return Delta.builder()
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .value(value)
            .price(price)
            .build();
    }

    protected HistoryValue buildFuturesDealResultBy(
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double value
    ) {
        return HistoryValue.builder()
            .ticker(ticker)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .build();
    }

    protected HistoryValue buildDeltaResultBy(
        String ticker,
        String tradeDate,
        double openPrice,
        double closePrice,
        double value
    ) {
        return HistoryValue.builder()
            .ticker(ticker)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .build();
    }

    protected HistoryValue buildDealResultBy(
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return HistoryValue.builder()
            .ticker(ticker)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
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

    protected HistoryValue.HistoryValueBuilder buildTradingResultWith(String ticker, LocalDate localDate) {
        return HistoryValue.builder()
            .tradeDate(localDate)
            .ticker(ticker)
            .openPrice(1.0)
            .closePrice(1.0)
            .lowPrice(1.0)
            .highPrice(1.0)
            .value(1.0)
            .waPrice(1D);
    }

    protected Deal buildDealWith(Long number, String ticker, LocalDateTime dateTime) {
        return Deal.builder()
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .value(10000.0)
            .isBuy(false)
            .qnt(1)
            .price(100D)
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
            .build();
    }
}
