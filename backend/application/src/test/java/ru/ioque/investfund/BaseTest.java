package ru.ioque.investfund;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.application.modules.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.application.modules.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.instrument.CurrencyPairDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.instrument.FuturesDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.instrument.IndexDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.instrument.StockDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.intraday.ContractDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.intraday.DealDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.intraday.DeltaDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.application.modules.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.fakes.FakeDIContainer;
import ru.ioque.investfund.fakes.FakeDatasourceRepository;
import ru.ioque.investfund.fakes.FakeDateTimeProvider;
import ru.ioque.investfund.fakes.FakeEmulatedPositionRepository;
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

    protected final FakeEmulatedPositionRepository emulatedPositionRepository() {
        return fakeDIContainer.getEmulatedPositionRepository();
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

    protected List<AggregatedHistoryDto> generateHistoryValues(
        String ticker,
        LocalDate start,
        LocalDate stop
    ) {
        final List<AggregatedHistoryDto> historyValues = new ArrayList<>();
        var cursor = start;
        while (cursor.isBefore(stop) || cursor.isEqual(stop)) {
            if (!cursor.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !cursor.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                historyValues.add(buildTradingResultWith(ticker, cursor).build());
            }
            cursor = cursor.plusDays(1);
        }
        return historyValues;
    }

    protected void integrateInstruments(DatasourceId datasourceId, InstrumentDto... instrumentDtos) {
        datasourceStorage().initInstrumentDetails(Arrays.asList(instrumentDtos));
        commandBus().execute(new IntegrateInstrumentsCommand(datasourceId));
    }

    protected void initInstrumentDetails(InstrumentDto... instrumentDtos) {
        datasourceStorage().initInstrumentDetails(Arrays.asList(instrumentDtos));
    }

    protected void initHistoryValues(List<AggregatedHistoryDto> tradingResults) {
        datasourceStorage().initHistoryValues(tradingResults);
    }

    protected void initHistoryValues(AggregatedHistoryDto... aggregatedHistoryDtos) {
        datasourceStorage().initHistoryValues(Arrays.asList(aggregatedHistoryDtos));
    }

    protected void initIntradayValues(IntradayDataDto... intradayValues) {
        datasourceStorage().initDealDatas(Arrays.asList(intradayValues));
    }

    protected List<IntradayData> getIntradayValuesBy(String ticker) {
        return fakeDIContainer.getIntradayValueRepository().getAllBy(Ticker.from(ticker)).toList();
    }

    protected List<AggregatedHistory> getHistoryValuesBy(String ticker) {
        return fakeDIContainer
            .getDatasourceRepository()
            .getInstrumentBy(Ticker.from(ticker))
            .getAggregateHistories()
            .stream()
            .toList();
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


    protected InstrumentId getInstrumentIdBy(String ticker) {
        return datasourceRepository().getInstrumentBy(Ticker.from(ticker)).getId();
    }

    protected String getMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElseThrow();
    }


    protected EnableUpdateInstrumentsCommand enableUpdateInstrumentCommandFrom(DatasourceId datasourceId, String... tickers) {
        return new EnableUpdateInstrumentsCommand(datasourceId, Arrays.stream(tickers).map(Ticker::from).toList());
    }

    protected EnableUpdateInstrumentsCommand disableUpdateInstrumentCommandFrom(DatasourceId datasourceId, String... tickers) {
        return new EnableUpdateInstrumentsCommand(datasourceId, Arrays.stream(tickers).map(Ticker::from).toList());
    }

    protected DealDto buildBuyDealBy(
        String ticker,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return DealDto.builder()
            .ticker(ticker)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .isBuy(true)
            .qnt(qnt)
            .price(price)
            .build();
    }

    protected DealDto buildSellDealBy(
        String ticker,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return DealDto.builder()
            .ticker(ticker)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .isBuy(false)
            .qnt(qnt)
            .price(price)
            .build();
    }

    protected ContractDto buildContractBy(
        String ticker,
        Long number,
        String localTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return ContractDto.builder()
            .ticker(ticker)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .price(price)
            .qnt(qnt)
            .value(value)
            .build();
    }

    protected DeltaDto buildDeltaBy(
        String ticker,
        Long number,
        String localTime,
        Double price,
        Double value
    ) {
        return DeltaDto.builder()
            .ticker(ticker)
            .number(number)
            .dateTime(dateTimeProvider().nowDate().atTime(LocalTime.parse(localTime)))
            .value(value)
            .price(price)
            .build();
    }

    protected AggregatedHistoryDto buildFuturesDealResultBy(
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double value
    ) {
        return AggregatedHistoryDto.builder()
            .ticker(ticker)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .build();
    }

    protected AggregatedHistoryDto buildDeltaResultBy(
        String ticker,
        String tradeDate,
        double openPrice,
        double closePrice,
        double value
    ) {
        return AggregatedHistoryDto.builder()
            .ticker(ticker)
            .tradeDate(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .build();
    }

    protected AggregatedHistoryDto buildDealResultBy(
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return AggregatedHistoryDto.builder()
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

    protected AggregatedHistoryDto.AggregatedHistoryDtoBuilder buildTradingResultWith(
        String ticker,
        LocalDate localDate
    ) {
        return AggregatedHistoryDto.builder()
            .ticker(ticker)
            .tradeDate(localDate)
            .openPrice(1.0)
            .closePrice(1.0)
            .lowPrice(1.0)
            .highPrice(1.0)
            .value(1.0)
            .waPrice(1D);
    }

    protected DealDto buildDealWith(String ticker, Long number, LocalDateTime dateTime) {
        return DealDto.builder()
            .ticker(ticker)
            .number(number)
            .dateTime(dateTime)
            .value(10000.0)
            .isBuy(false)
            .qnt(1)
            .price(100D)
            .build();
    }

    protected IndexDto imoex() {
        return IndexDto
            .builder()
            .ticker(IMOEX)
            .name("Индекс МосБиржи")
            .shortName("Индекс МосБиржи")
            .annualLow(100D)
            .annualHigh(100D)
            .build();
    }

    protected StockDto afks() {
        return StockDto
            .builder()
            .ticker(AFKS)
            .shortName("ао Система")
            .name("АФК Система")
            .lotSize(10000)
            .regNumber("1-05-01669-A")
            .isin("RU000A0DQZE3")
            .listLevel(1)
            .build();
    }

    protected StockDto sberp() {
        return StockDto
            .builder()
            .ticker(SBERP)
            .shortName("Сбер п")
            .name("Сбербанк П")
            .lotSize(100)
            .regNumber("20301481B")
            .isin("RU0009029557")
            .listLevel(1)
            .build();
    }

    protected StockDto sber() {
        return StockDto
            .builder()
            .ticker(SBER)
            .shortName("Сбер")
            .name("Сбербанк")
            .lotSize(100)
            .regNumber("10301481B")
            .isin("RU0009029540")
            .listLevel(1)
            .build();
    }

    protected StockDto sibn() {
        return StockDto
            .builder()
            .ticker(SIBN)
            .shortName("Газпромнефть")
            .name("Газпромнефть")
            .lotSize(100)
            .regNumber("1-01-00146-A")
            .isin("RU0009062467")
            .listLevel(1)
            .build();
    }

    protected FuturesDto brf4() {
        return FuturesDto
            .builder()
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

    protected StockDto lkohDetails() {
        return StockDto
            .builder()
            .ticker(LKOH)
            .shortName("Лукойл")
            .name("Лукойл")
            .lotSize(100)
            .regNumber("1-01-00077-A")
            .isin("RU0009024277")
            .listLevel(1)
            .build();
    }

    protected StockDto tatnDetails() {
        return StockDto
            .builder()
            .ticker(TATN)
            .shortName("Татнефть")
            .name("Татнефть")
            .isin("RU0009033591")
            .regNumber("1-03-00161-A")
            .lotSize(100)
            .listLevel(1)
            .build();
    }

    protected StockDto rosnDetails() {
        return StockDto
            .builder()
            .ticker(ROSN)
            .shortName("Роснефть")
            .name("Роснефть")
            .isin("RU000A0J2Q06")
            .regNumber("1-02-00122-A")
            .lotSize(100)
            .listLevel(1)
            .build();
    }

    protected CurrencyPairDto usdRubDetails() {
        return CurrencyPairDto
            .builder()
            .ticker(USD000UTSTOM)
            .shortName("USDRUB_TOM")
            .name("USDRUB_TOM - USD/РУБ")
            .faceUnit("RUB")
            .lotSize(1000)
            .build();
    }

    protected StockDto tgkbDetails() {
        return StockDto
            .builder()
            .ticker(TGKB)
            .name("TGKB")
            .shortName("TGKB")
            .isin("RU000A0JNGS7")
            .regNumber("1-01-10420-A")
            .listLevel(3)
            .lotSize(100)
            .build();
    }

    protected StockDto tgknDetails() {
        return StockDto
            .builder()
            .ticker(TGKN)
            .name("TGKN")
            .shortName("TGKN")
            .lotSize(100)
            .isin("RU000A0H1ES3")
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

    protected IntradayDataDto buildImoexDelta(Long number, String localTime, Double price, Double value) {
        return buildDeltaBy(IMOEX, number, localTime, price, value);
    }

    protected AggregatedHistoryDto buildImoexHistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return buildDeltaResultBy(IMOEX, tradeDate, openPrice, closePrice, value);
    }

    protected IntradayDataDto buildTgknSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildSellDealBy(TGKN, number, localTime, price, value, qnt);
    }

    protected IntradayDataDto buildTgknBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TGKN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildTgknHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(TGKN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildTgkbSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildSellDealBy(TGKB, number, localTime, price, value, qnt);
    }

    protected IntradayDataDto buildTgkbBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TGKB, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildTgkbHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(TGKB, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildTatnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(TATN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildTatnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(TATN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildBrf4Contract(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildContractBy(BRF4, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildBrf4HistoryValue(String tradeDate, Double openPrice, Double closePrice, Double value) {
        return buildFuturesDealResultBy(BRF4, tradeDate, openPrice, closePrice, value);
    }

    protected IntradayDataDto buildLkohBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(LKOH, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildLkohHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(LKOH, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildSibnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SIBN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildSibnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(SIBN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildRosnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(ROSN, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildRosnHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(ROSN, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildSberBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SBER, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildSberHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(SBER, tradeDate, openPrice, closePrice, waPrice, value);
    }

    protected IntradayDataDto buildSberpBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return buildBuyDealBy(SBERP, number, localTime, price, value, qnt);
    }

    protected AggregatedHistoryDto buildSberpHistoryValue(
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return buildDealResultBy(SBERP, tradeDate, openPrice, closePrice, waPrice, value);
    }
}
