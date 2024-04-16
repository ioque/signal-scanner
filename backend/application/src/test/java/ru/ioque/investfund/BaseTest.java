package ru.ioque.investfund;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import ru.ioque.investfund.application.modules.CommandBus;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
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
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.fakes.FakeDIContainer;
import ru.ioque.investfund.fakes.FakeDatasourceRepository;
import ru.ioque.investfund.fakes.FakeDateTimeProvider;
import ru.ioque.investfund.fakes.FakeEventPublisher;
import ru.ioque.investfund.fakes.FakeLoggerProvider;
import ru.ioque.investfund.fakes.FakeScannerRepository;
import ru.ioque.investfund.fakes.FakeTradingSnapshotsProvider;
import ru.ioque.investfund.fixture.ExchangeDataFixture;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BaseTest {
    private final FakeDIContainer fakeDIContainer = new FakeDIContainer();

    protected ExchangeDataFixture exchangeDataFixture() {
        return fakeDIContainer.getExchangeDataFixture();
    }

    protected FakeDatasourceRepository datasourceRepository() {
        return fakeDIContainer.getDatasourceRepository();
    }

    protected FakeDateTimeProvider dateTimeProvider() {
        return fakeDIContainer.getDateTimeProvider();
    }

    protected FakeScannerRepository scannerRepository() {
        return fakeDIContainer.getScannerRepository();
    }

    protected FakeTradingSnapshotsProvider tradingDataRepository() {
        return fakeDIContainer.getTradingDataRepository();
    }

    protected final FakeLoggerProvider loggerProvider() {
        return fakeDIContainer.getLoggerProvider();
    }

    protected final CommandBus commandBus() {
        return fakeDIContainer.getCommandBus();
    }

    protected final FakeEventPublisher eventPublisher() {
        return fakeDIContainer.getEventPublisher();
    }

    protected LocalDate nowMinus1Days() {
        return dateTimeProvider().nowDateTime().toLocalDate().minusDays(1);
    }

    protected LocalDate nowMinus3Month() {
        return dateTimeProvider().nowDateTime().minusMonths(3).toLocalDate();
    }

    protected List<Instrument> getInstruments(UUID datasourceId) {
        return datasourceRepository()
            .getBy(datasourceId)
            .map(Datasource::getInstruments)
            .orElse(new ArrayList<>());
    }

    protected void initTodayDateTime(String dateTime) {
        dateTimeProvider().setNow(LocalDateTime.parse(dateTime));
    }

    protected List<HistoryValue> generateTradingResultsBy(
        UUID datasourceId,
        String ticker,
        LocalDate start,
        LocalDate stop
    ) {
        final List<HistoryValue> historyValues = new ArrayList<>();
        var cursor = start;
        while (cursor.isBefore(stop) || cursor.isEqual(stop)) {
            if (!cursor.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !cursor.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                historyValues.add(buildTradingResultWith(datasourceId, ticker, cursor).build());
            }
            cursor = cursor.plusDays(1);
        }
        return historyValues;
    }

    protected void integrateInstruments(UUID datasourceId, Instrument... instruments) {
        exchangeDataFixture().initInstruments(Arrays.asList(instruments));
        commandBus().execute(new IntegrateInstrumentsCommand(datasourceId));
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

    protected List<IntradayValue> getIntradayValuesBy(UUID datasourceId, String ticker) {
        return fakeDIContainer.getIntradayValueRepository().getAllBy(datasourceId, ticker).toList();
    }

    protected UUID getDatasourceId() {
        return datasourceRepository().getAll().get(0).getId();
    }

    protected List<IntradayValue> getIntradayValuesBy(UUID datasourceId) {
        return fakeDIContainer.getIntradayValueRepository().getAllBy(datasourceId).toList();
    }

    protected List<HistoryValue> getHistoryValuesBy(UUID datasourceId, String ticker) {
        return fakeDIContainer.getHistoryValueRepository().getAllBy(datasourceId, ticker).toList();
    }

    protected List<HistoryValue> getHistoryValues(UUID datasourceId) {
        return fakeDIContainer.getHistoryValueRepository().getAllBy(datasourceId).toList();
    }

    protected void clearLogs() {
        loggerProvider().clearLogs();
    }

    protected LocalDateTime getToday() {
        return dateTimeProvider().nowDateTime();
    }

    protected void runWorkPipeline(UUID datasourceId) {
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        commandBus().execute(new ProduceSignalCommand(datasourceId, getToday()));
    }

    protected void runWorkPipelineAndClearLogs(UUID datasourceId) {
        runWorkPipeline(datasourceId);
        loggerProvider().clearLogs();
    }

    protected List<String> getTickers(UUID datasourceId) {
        return getInstruments(datasourceId).stream().map(Instrument::getTicker).toList();
    }

    protected String getMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElseThrow();
    }

    protected Deal buildBuyDealBy(
        UUID datasourceId,
        Long number,
        String ticker,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Deal.builder()
            .datasourceId(datasourceId)
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
        UUID datasourceId,
        Long number,
        String ticker,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Deal.builder()
            .datasourceId(datasourceId)
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
        UUID datasourceId,
        Long number,
        String ticker,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Contract.builder()
            .datasourceId(datasourceId)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .price(price)
            .qnt(qnt)
            .value(value)
            .build();
    }

    protected Delta buildDeltaBy(
        UUID datasourceId,
        Long number,
        String ticker,
        String localTime,
        Double price,
        Double value
    ) {
        return Delta.builder()
            .datasourceId(datasourceId)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .ticker(ticker)
            .value(value)
            .price(price)
            .build();
    }

    protected HistoryValue buildFuturesDealResultBy(
        UUID datasourceId,
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double value
    ) {
        return HistoryValue.builder()
            .datasourceId(datasourceId)
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
        UUID datasourceId,
        String ticker,
        String tradeDate,
        double openPrice,
        double closePrice,
        double value
    ) {
        return HistoryValue.builder()
            .datasourceId(datasourceId)
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
        UUID datasourceId,
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return HistoryValue.builder()
            .datasourceId(datasourceId)
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

    protected HistoryValue.HistoryValueBuilder buildTradingResultWith(
        UUID datasourceId,
        String ticker,
        LocalDate localDate
    ) {
        return HistoryValue.builder()
            .datasourceId(datasourceId)
            .tradeDate(localDate)
            .ticker(ticker)
            .openPrice(1.0)
            .closePrice(1.0)
            .lowPrice(1.0)
            .highPrice(1.0)
            .value(1.0)
            .waPrice(1D);
    }

    protected Deal buildDealWith(UUID datasourceId, Long number, String ticker, LocalDateTime dateTime) {
        return Deal.builder()
            .datasourceId(datasourceId)
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
