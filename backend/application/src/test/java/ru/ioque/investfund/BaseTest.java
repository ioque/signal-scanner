package ru.ioque.investfund;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.command.PublishAggregatedHistory;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.application.modules.scanner.processor.SearchContextManager;
import ru.ioque.investfund.application.modules.scanner.processor.StreamingScannerEngine;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetail;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetail;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetail;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.details.StockDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.fakes.FakeDIContainer;
import ru.ioque.investfund.fakes.FakeDatasourceRepository;
import ru.ioque.investfund.fakes.FakeDateTimeProvider;
import ru.ioque.investfund.fakes.FakeEmulatedPositionRepository;
import ru.ioque.investfund.fakes.FakeIntradayJournal;
import ru.ioque.investfund.fakes.FakeLoggerProvider;
import ru.ioque.investfund.fakes.FakeScannerRepository;
import ru.ioque.investfund.fakes.FakeSignalJournal;
import ru.ioque.investfund.fakes.FakeTelegramChatRepository;
import ru.ioque.investfund.fakes.FakeTelegramMessageSender;
import ru.ioque.investfund.fixture.DatasourceStorage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseTest {
    private final FakeDIContainer fakeDIContainer = new FakeDIContainer();

    protected DatasourceStorage datasourceStorage() {
        return fakeDIContainer.getDatasourceStorage();
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

    protected final FakeLoggerProvider loggerProvider() {
        return fakeDIContainer.getLoggerProvider();
    }

    protected final CommandBus commandBus() {
        return fakeDIContainer.getCommandBus();
    }

    protected final FakeTelegramChatRepository telegramChatRepository() {
        return fakeDIContainer.getTelegramChatRepository();
    }

    protected final FakeTelegramMessageSender telegramMessageSender() {
        return fakeDIContainer.getTelegramMessageSender();
    }

    protected final FakeEmulatedPositionRepository emulatedPositionRepository() {
        return fakeDIContainer.getEmulatedPositionRepository();
    }

    protected final FakeIntradayJournal intradayJournal() {
        return fakeDIContainer.getIntradayJournal();
    }

    protected final StreamingScannerEngine streamingScannerEngine() {
        return fakeDIContainer.getStreamingScannerEngine();
    }

    protected final SearchContextManager searchContextManager() {
        return fakeDIContainer.getSearchContextManager();
    }

    protected final FakeSignalJournal signalJournal() {
        return fakeDIContainer.getSignalJournal();
    }

    protected LocalDate nowMinus1Days() {
        return dateTimeProvider().nowDateTime().toLocalDate().minusDays(1);
    }

    protected LocalDate nowMinus3Month() {
        return dateTimeProvider().nowDateTime().minusMonths(3).toLocalDate();
    }

    protected List<Instrument> getInstruments(DatasourceId datasourceId) {
        return datasourceRepository()
            .findBy(datasourceId)
            .map(Datasource::getInstruments)
            .orElse(new ArrayList<>());
    }

    protected void initTodayDateTime(String dateTime) {
        dateTimeProvider().setNow(LocalDateTime.parse(dateTime));
    }

    protected List<AggregatedHistory> generateHistoryValues(
        String ticker,
        LocalDate start,
        LocalDate stop
    ) {
        final List<AggregatedHistory> historyValues = new ArrayList<>();
        var cursor = start;
        while (cursor.isBefore(stop) || cursor.isEqual(stop)) {
            if (!cursor.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !cursor.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                historyValues.add(buildAggregatedHistory(ticker, cursor).build());
            }
            cursor = cursor.plusDays(1);
        }
        return historyValues;
    }


    protected void initInstrumentDetails(InstrumentDetail... instrumentDetails) {
        datasourceStorage().initInstrumentDetails(Arrays.asList(instrumentDetails));
    }

    protected void initHistoryValues(List<AggregatedHistory> tradingResults) {
        datasourceStorage().initHistoryValues(tradingResults);
    }

    protected void initHistoryValues(AggregatedHistory... aggregatedHistoryDtos) {
        datasourceStorage().initHistoryValues(Arrays.asList(aggregatedHistoryDtos));
    }

    protected void initIntradayValues(IntradayData... intradayValues) {
        datasourceStorage().initDealDatas(Arrays.asList(intradayValues));
    }

    protected List<IntradayData> getIntradayValuesBy(String ticker) {
        return fakeDIContainer.getIntradayJournal()
            .stream()
            .filter(row -> row.getTicker().equals(Ticker.from(ticker)))
            .toList();
    }

    protected List<AggregatedHistory> getHistoryValuesBy(String ticker) {
        return fakeDIContainer
            .getAggregatedHistoryJournal()
            .findAllBy(Ticker.from(ticker));
    }

    protected DatasourceId getDatasourceId() {
        return datasourceRepository().getAll().get(0).getId();
    }

    protected void clearLogs() {
        loggerProvider().clearLogs();
    }

    protected LocalDateTime getToday() {
        return dateTimeProvider().nowDateTime();
    }

    protected void runWorkPipeline(DatasourceId datasourceId) {
        commandBus().execute(new PublishAggregatedHistory(getDatasourceId()));
        commandBus().execute(new PublishIntradayData(datasourceId));
        searchContextManager().initSearchContext(scannerRepository().findAllBy(getDatasourceId()));
        statisticStream().forEach(streamingScannerEngine()::process);
    }

    private List<IntradayPerformance> statisticStream() {
        final Map<Ticker, List<IntradayData>> tickerToIntraday = intradayJournal()
            .stream()
            .collect(Collectors.groupingBy(IntradayData::getTicker));
        final List<IntradayPerformance> statistics = new ArrayList<>();
        tickerToIntraday.forEach((ticker, intradayDataList) -> {
            IntradayPerformance intradayPerformance = IntradayPerformance.empty();
            List<IntradayData> sorted = intradayDataList.stream().sorted().toList();
            for (var intradayData : sorted) {
                intradayPerformance = intradayPerformance.add(ticker.getValue(), intradayData);
                statistics.add(intradayPerformance);
            }
        });
        return statistics.stream().sorted().toList();
    }

    protected void runWorkPipelineAndClearLogs(DatasourceId datasourceId) {
        runWorkPipeline(datasourceId);
        loggerProvider().clearLogs();
    }

    protected List<Ticker> getTickers(DatasourceId datasourceId) {
        return getInstruments(datasourceId).stream().map(Instrument::getTicker).toList();
    }


    protected InstrumentId getInstrumentIdBy(String ticker) {
        return datasourceRepository().getInstrumentBy(Ticker.from(ticker)).getId();
    }

    protected String getMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElseThrow();
    }


    protected EnableUpdateInstruments enableUpdateInstrumentCommandFrom(DatasourceId datasourceId, String... tickers) {
        return new EnableUpdateInstruments(datasourceId, Arrays.stream(tickers).map(Ticker::from).toList());
    }

    protected EnableUpdateInstruments disableUpdateInstrumentCommandFrom(DatasourceId datasourceId, String... tickers) {
        return new EnableUpdateInstruments(datasourceId, Arrays.stream(tickers).map(Ticker::from).toList());
    }

    protected Deal buildBuyDealBy(
        String ticker,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Deal.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .isBuy(true)
            .qnt(qnt)
            .price(price)
            .build();
    }

    protected Deal buildSellDealBy(
        String ticker,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Deal.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .isBuy(false)
            .qnt(qnt)
            .price(price)
            .build();
    }

    protected Contract buildContractBy(
        String ticker,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Contract.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .price(price)
            .qnt(qnt)
            .value(value)
            .build();
    }

    protected Delta buildDeltaBy(
        String ticker,
        Long number,
        String localTime,
        Double price,
        Double value
    ) {
        return Delta.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .price(price)
            .build();
    }

    protected AggregatedHistory buildAggregatedHistory(
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double value
    ) {
        return AggregatedHistory.builder()
            .ticker(Ticker.from(ticker))
            .date(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .build();
    }

    protected AggregatedHistory buildAggregatedHistory(
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return AggregatedHistory.builder()
            .ticker(Ticker.from(ticker))
            .date(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .waPrice(waPrice)
            .build();
    }

    protected AggregatedHistory.AggregatedHistoryBuilder buildAggregatedHistory(
        String ticker,
        LocalDate localDate
    ) {
        return AggregatedHistory.builder()
            .ticker(Ticker.from(ticker))
            .date(localDate)
            .openPrice(1.0)
            .closePrice(1.0)
            .lowPrice(1.0)
            .highPrice(1.0)
            .value(1.0)
            .waPrice(1D);
    }

    protected Deal buildDealWith(String ticker, Long number, LocalDateTime dateTime) {
        return Deal.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .dateTime(dateTime)
            .value(10000.0)
            .isBuy(false)
            .qnt(1)
            .price(100D)
            .build();
    }

    protected IndexDetail imoexDetails() {
        return IndexDetail
            .builder()
            .ticker(Ticker.from(IMOEX))
            .name("Индекс МосБиржи")
            .shortName("Индекс МосБиржи")
            .annualLow(100D)
            .annualHigh(100D)
            .build();
    }

    protected StockDetail afks() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(AFKS))
            .shortName("ао Система")
            .name("АФК Система")
            .lotSize(10000)
            .regNumber("1-05-01669-A")
            .isin(Isin.from("RU000A0DQZE3"))
            .listLevel(1)
            .build();
    }

    protected StockDetail sberp() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(SBERP))
            .shortName("Сбер п")
            .name("Сбербанк П")
            .lotSize(100)
            .regNumber("20301481B")
            .isin(Isin.from("RU0009029557"))
            .listLevel(1)
            .build();
    }

    protected StockDetail sber() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(SBER))
            .shortName("Сбер")
            .name("Сбербанк")
            .lotSize(100)
            .regNumber("10301481B")
            .isin(Isin.from("RU0009029540"))
            .listLevel(1)
            .build();
    }

    protected StockDetail sibn() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(SIBN))
            .shortName("Газпромнефть")
            .name("Газпромнефть")
            .lotSize(100)
            .regNumber("1-01-00146-A")
            .isin(Isin.from("RU0009062467"))
            .listLevel(1)
            .build();
    }

    protected FuturesDetail brf4() {
        return FuturesDetail
            .builder()
            .ticker(Ticker.from(BRF4))
            .name("Фьючерсный контракт BR-1.24")
            .shortName("BR-1.24")
            .assetCode("BR")
            .lowLimit(100D)
            .highLimit(100D)
            .initialMargin(100D)
            .lotVolume(1000)
            .build();
    }

    protected StockDetail lkohDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(LKOH))
            .shortName("Лукойл")
            .name("Лукойл")
            .lotSize(100)
            .regNumber("1-01-00077-A")
            .isin(Isin.from("RU0009024277"))
            .listLevel(1)
            .build();
    }

    protected StockDetail tatnDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(TATN))
            .shortName("Татнефть")
            .name("Татнефть")
            .isin(Isin.from("RU0009033591"))
            .regNumber("1-03-00161-A")
            .lotSize(100)
            .listLevel(1)
            .build();
    }

    protected StockDetail rosnDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(ROSN))
            .shortName("Роснефть")
            .name("Роснефть")
            .isin(Isin.from("RU000A0J2Q06"))
            .regNumber("1-02-00122-A")
            .lotSize(100)
            .listLevel(1)
            .build();
    }

    protected CurrencyPairDetail usdRubDetails() {
        return CurrencyPairDetail
            .builder()
            .ticker(Ticker.from(USD000UTSTOM))
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .faceUnit("RUB")
            .lotSize(1000)
            .build();
    }

    protected StockDetail tgkbDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(TGKB))
            .name("TGKB")
            .shortName("TGKB")
            .isin(Isin.from("RU000A0JNGS7"))
            .regNumber("1-01-10420-A")
            .listLevel(3)
            .lotSize(100)
            .build();
    }

    protected StockDetail tgknDetails() {
        return StockDetail
            .builder()
            .ticker(Ticker.from(TGKN))
            .name("TGKN")
            .shortName("TGKN")
            .lotSize(100)
            .isin(Isin.from("RU000A0H1ES3"))
            .regNumber("1-01-22451-F")
            .listLevel(2)
            .build();
    }

    protected final String TGKN = "TGKN";
    protected final String TGKB = "TGKB";
    protected final String ROSN = "ROSN";
    protected final String TATN = "TATN";
    protected final String LKOH = "LKOH";
    protected final String SBER = "SBER";
    protected final String SBERP = "SBERP";
    protected final String SIBN = "SIBN";
    protected final String AFKS = "AFKS";
    protected final String USD000UTSTOM = "USD000UTSTOM";
    protected final String BRF4 = "BRF4";
    protected final String IMOEX = "IMOEX";

    protected IntradayData buildImoexDelta(Long number, String localTime, Double price, Double value) {
        return buildDeltaBy(IMOEX, number, localTime, price, value);
    }

    protected AggregatedHistory buildImoexHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return buildAggregatedHistory(IMOEX, tradeDate, openPrice, closePrice, value);
    }

    protected IntradayData buildTgknSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildSellDealBy(TGKN, number, localTime, price, value, qnt);
    }

    protected IntradayData buildTgknBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TGKN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildTgknHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildAggregatedHistory(TGKN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayData buildTgkbSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildSellDealBy(TGKB, number, localTime, price, value, qnt);
    }

    protected IntradayData buildTgkbBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TGKB, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildTgkbHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildAggregatedHistory(TGKB, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayData buildTatnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TATN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildTatnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildAggregatedHistory(TATN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayData buildBrf4Contract(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildContractBy(BRF4, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildBrf4HistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return buildAggregatedHistory(BRF4, tradeDate, openPrice, closePrice, value);
    }

    protected IntradayData buildLkohBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(LKOH, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildLkohHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildAggregatedHistory(LKOH, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayData buildSibnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SIBN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildSibnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildAggregatedHistory(SIBN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayData buildRosnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(ROSN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildRosnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildAggregatedHistory(ROSN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayData buildSberBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SBER, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildSberHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildAggregatedHistory(SBER, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayData buildSberpBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SBERP, number, localTime, price, value, qnt);
    }

    protected AggregatedHistory buildSberpHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildAggregatedHistory(SBERP, tradeDate, openPrice, closePrice, waPrice, value);
    }
}
