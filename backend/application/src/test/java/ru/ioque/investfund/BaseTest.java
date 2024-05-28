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
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedHistory;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.fakes.FakeDIContainer;
import ru.ioque.investfund.fakes.FakeDatasourceRepository;
import ru.ioque.investfund.fakes.FakeDateTimeProvider;
import ru.ioque.investfund.fakes.FakeEmulatedPositionJournal;
import ru.ioque.investfund.fakes.journal.FakeIntradayJournal;
import ru.ioque.investfund.fakes.FakeLoggerProvider;
import ru.ioque.investfund.fakes.FakeScannerRepository;
import ru.ioque.investfund.fakes.journal.FakeSignalJournal;
import ru.ioque.investfund.fakes.FakeTelegramChatRepository;
import ru.ioque.investfund.fakes.FakeTelegramMessageSender;
import ru.ioque.investfund.fakes.DatasourceStorage;
import ru.ioque.investfund.fixture.AggregatedHistoryFixture;
import ru.ioque.investfund.fixture.InstrumentDetailsFixture;
import ru.ioque.investfund.fixture.IntradayDataFixture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseTest {
    private final FakeDIContainer fakeDIContainer = new FakeDIContainer();

    protected final InstrumentDetailsFixture instrumentFixture = new InstrumentDetailsFixture();
    protected final IntradayDataFixture intradayFixture = new IntradayDataFixture(dateTimeProvider());
    protected final AggregatedHistoryFixture historyFixture = new AggregatedHistoryFixture();

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

    protected final FakeEmulatedPositionJournal emulatedPositionRepository() {
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
}
