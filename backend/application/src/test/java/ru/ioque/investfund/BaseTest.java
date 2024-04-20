package ru.ioque.investfund;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import ru.ioque.investfund.application.CommandBus;
import ru.ioque.investfund.application.telegrambot.TelegramBotService;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.datasource.entity.CurrencyPair;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Futures;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.Stock;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.Contract;
import ru.ioque.investfund.domain.datasource.value.Deal;
import ru.ioque.investfund.domain.datasource.value.Delta;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.Ticker;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.fakes.FakeDIContainer;
import ru.ioque.investfund.fakes.FakeDatasourceRepository;
import ru.ioque.investfund.fakes.FakeDateTimeProvider;
import ru.ioque.investfund.fakes.FakeEventPublisher;
import ru.ioque.investfund.fakes.FakeLoggerProvider;
import ru.ioque.investfund.fakes.FakeScannerRepository;
import ru.ioque.investfund.fakes.FakeTelegramChatRepository;
import ru.ioque.investfund.fakes.FakeTelegramMessageSender;
import ru.ioque.investfund.fakes.FakeTradingSnapshotsRepository;
import ru.ioque.investfund.fixture.DatasourceStorage;

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

    protected FakeTradingSnapshotsRepository tradingDataRepository() {
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

    protected final FakeTelegramChatRepository telegramChatRepository() {
        return fakeDIContainer.getTelegramChatRepository();
    }

    protected final FakeTelegramMessageSender telegramMessageSender() {
        return fakeDIContainer.getTelegramMessageSender();
    }

    protected final TelegramBotService telegramBotService() {
        return fakeDIContainer.getTelegramBotService();
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

    protected List<HistoryValue> generateTradingResultsBy(
        InstrumentId instrumentId,
        LocalDate start,
        LocalDate stop
    ) {
        final List<HistoryValue> historyValues = new ArrayList<>();
        var cursor = start;
        while (cursor.isBefore(stop) || cursor.isEqual(stop)) {
            if (!cursor.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !cursor.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                historyValues.add(buildTradingResultWith(instrumentId, cursor).build());
            }
            cursor = cursor.plusDays(1);
        }
        return historyValues;
    }

    protected void integrateInstruments(DatasourceId datasourceId, Instrument... instruments) {
        datasourceStorage().initInstruments(Arrays.asList(instruments));
        commandBus().execute(new IntegrateInstrumentsCommand(datasourceId));
    }

    protected void initInstruments(Instrument... instruments) {
        datasourceStorage().initInstruments(Arrays.asList(instruments));
    }

    protected void initTradingResults(List<HistoryValue> tradingResults) {
        datasourceStorage().initTradingResults(tradingResults);
    }

    protected void initTradingResults(HistoryValue... tradingResults) {
        initTradingResults(Arrays.asList(tradingResults));
    }

    protected void initDealDatas(IntradayValue... intradayValues) {
        datasourceStorage().initDealDatas(Arrays.asList(intradayValues));
    }

    protected List<IntradayValue> getIntradayValuesBy(InstrumentId instrumentId) {
        return fakeDIContainer.getIntradayValueRepository().getAllBy(instrumentId).toList();
    }

    protected List<HistoryValue> getHistoryValuesBy(InstrumentId instrumentId) {
        return fakeDIContainer.getHistoryValueRepository().getAllBy(instrumentId).toList();
    }

    protected DatasourceId getDatasourceId() {
        return datasourceRepository().findAll().get(0).getId();
    }

    protected void clearLogs() {
        loggerProvider().clearLogs();
    }

    protected LocalDateTime getToday() {
        return dateTimeProvider().nowDateTime();
    }

    protected void runWorkPipeline(DatasourceId datasourceId) {
        commandBus().execute(new IntegrateTradingDataCommand(datasourceId));
        commandBus().execute(new ProduceSignalCommand(datasourceId, getToday()));
    }

    protected void runWorkPipelineAndClearLogs(DatasourceId datasourceId) {
        runWorkPipeline(datasourceId);
        loggerProvider().clearLogs();
    }

    protected List<Ticker> getTickers(DatasourceId datasourceId) {
        return getInstruments(datasourceId).stream().map(Instrument::getTicker).toList();
    }

    protected String getMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElseThrow();
    }

    protected Deal buildBuyDealBy(
        InstrumentId instrumentId,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Deal.builder()
            .instrumentId(instrumentId)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .isBuy(true)
            .qnt(qnt)
            .price(price)
            .build();
    }

    protected Deal buildSellDealBy(
        InstrumentId instrumentId,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Deal.builder()
            .instrumentId(instrumentId)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .isBuy(false)
            .qnt(qnt)
            .price(price)
            .build();
    }

    protected Contract buildContractBy(
        InstrumentId instrumentId,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Contract.builder()
            .instrumentId(instrumentId)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .price(price)
            .qnt(qnt)
            .value(value)
            .build();
    }

    protected Delta buildDeltaBy(
        InstrumentId instrumentId,
        Long number,
        String localTime,
        Double price,
        Double value
    ) {
        return Delta.builder()
            .instrumentId(instrumentId)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .price(price)
            .build();
    }

    protected HistoryValue buildFuturesDealResultBy(
        InstrumentId instrumentId,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double value
    ) {
        return HistoryValue.builder()
            .instrumentId(instrumentId)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .build();
    }

    protected HistoryValue buildDeltaResultBy(
        InstrumentId instrumentId,
        String tradeDate,
        double openPrice,
        double closePrice,
        double value
    ) {
        return HistoryValue.builder()
            .instrumentId(instrumentId)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .build();
    }

    protected HistoryValue buildDealResultBy(
        InstrumentId instrumentId,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return HistoryValue.builder()
            .instrumentId(instrumentId)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .waPrice(waPrice)
            .build();
    }

    protected HistoryValue.HistoryValueBuilder buildTradingResultWith(
        InstrumentId instrumentId,
        LocalDate localDate
    ) {
        return HistoryValue.builder()
            .instrumentId(instrumentId)
            .tradeDate(localDate)
            .openPrice(1.0)
            .closePrice(1.0)
            .lowPrice(1.0)
            .highPrice(1.0)
            .value(1.0)
            .waPrice(1D);
    }

    protected Deal buildDealWith(InstrumentId instrumentId, Long number, LocalDateTime dateTime) {
        return Deal.builder()
            .instrumentId(instrumentId)
            .number(number)
            .dateTime(dateTime)
            .value(10000.0)
            .isBuy(false)
            .qnt(1)
            .price(100D)
            .build();
    }

    protected Index imoex() {
        return Index
            .builder()
            .id(imoexId)
            .ticker(IMOEX)
            .name("Индекс МосБиржи")
            .shortName("Индекс МосБиржи")
            .annualLow(100D)
            .annualHigh(100D)
            .build();
    }

    protected Stock afks() {
        return Stock
            .builder()
            .id(afksId)
            .ticker(AFKS)
            .shortName("ао Система")
            .name("АФК Система")
            .lotSize(10000)
            .regNumber("regNumber")
            .isin("isin")
            .listLevel(1)
            .build();
    }

    protected Stock sberP() {
        return Stock
            .builder()
            .id(sberpId)
            .ticker(SBERP)
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
            .id(sberId)
            .ticker(SBER)
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
            .id(sibnId)
            .ticker(SIBN)
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
            .id(brf4Id)
            .ticker(BRF4)
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
            .id(lkohId)
            .ticker(LKOH)
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
            .id(tatnId)
            .ticker(TATN)
            .shortName("Татнефть")
            .name("Татнефть")
            .lotSize(100)
            .build();
    }

    protected Stock rosn() {
        return Stock
            .builder()
            .id(rosnId)
            .ticker(ROSN)
            .shortName("Роснефть")
            .name("Роснефть")
            .lotSize(100)
            .build();
    }

    protected CurrencyPair usdRub() {
        return CurrencyPair
            .builder()
            .id(usdRubId)
            .ticker(USD000UTSTOM)
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .faceUnit("RUB")
            .lotSize(1000)
            .build();
    }

    protected Stock tgkb() {
        return Stock
            .builder()
            .id(tgkbId)
            .ticker(TGKB)
            .name("TGKB")
            .shortName("TGKB")
            .lotSize(100)
            .build();
    }

    protected Stock tgkn() {
        return Stock
            .builder()
            .id(tgknId)
            .ticker(TGKN)
            .name("TGKN")
            .shortName("TGKN")
            .lotSize(100)
            .build();
    }

    protected final Ticker TGKN = Ticker.from("TGKN");
    protected final Ticker TGKB = Ticker.from("TGKB");
    protected final Ticker ROSN = Ticker.from("ROSN");
    protected final Ticker TATN = Ticker.from("TATN");
    protected final Ticker LKOH = Ticker.from("LKOH");
    protected final Ticker SBER = Ticker.from("SBER");
    protected final Ticker SBERP = Ticker.from("SBERP");
    protected final Ticker SIBN = Ticker.from("SIBN");
    protected final Ticker AFKS = Ticker.from("AFKS");
    protected final Ticker USD000UTSTOM = Ticker.from("USD000UTSTOM");
    protected final Ticker BRF4 = Ticker.from("BRF4");
    protected final Ticker IMOEX = Ticker.from("IMOEX");
    protected InstrumentId tgknId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId tgkbId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId rosnId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId tatnId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId lkohId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId sberId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId sberpId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId sibnId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId afksId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId usdRubId = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId brf4Id = InstrumentId.from(UUID.randomUUID());
    protected InstrumentId imoexId = InstrumentId.from(UUID.randomUUID());
}
