package ru.ioque.investfund;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.pipeline.PipelineManager;
import ru.ioque.investfund.application.modules.pipeline.sink.SignalRegistry;
import ru.ioque.investfund.application.modules.pipeline.transformer.PerformanceCalculator;
import ru.ioque.investfund.application.modules.pipeline.transformer.SignalsFinder;
import ru.ioque.investfund.application.modules.pipeline.transformer.SignalsFinderContext;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.fakes.FakeDIContainer;
import ru.ioque.investfund.fakes.repository.FakeDatasourceRepository;
import ru.ioque.investfund.fakes.FakeDateTimeProvider;
import ru.ioque.investfund.fakes.repository.FakeAggregatedTotalsRepository;
import ru.ioque.investfund.fakes.repository.FakeIntradayDataJournal;
import ru.ioque.investfund.fakes.FakeLoggerProvider;
import ru.ioque.investfund.fakes.repository.FakeScannerRepository;
import ru.ioque.investfund.fakes.repository.FakeSignalRepository;
import ru.ioque.investfund.fakes.repository.FakeTelegramChatRepository;
import ru.ioque.investfund.fakes.FakeTelegramMessageSender;
import ru.ioque.investfund.fakes.DatasourceStorage;
import ru.ioque.investfund.fixture.AggregatedHistoryFixture;
import ru.ioque.investfund.fixture.InstrumentDetailsFixture;
import ru.ioque.investfund.fixture.IntradayDataFixture;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    protected final FakeIntradayDataJournal intradayJournal() {
        return fakeDIContainer.getIntradayDataRepository();
    }

    protected final FakeAggregatedTotalsRepository aggregatedTotalsJournal() {
        return fakeDIContainer.getAggregatedTotalsJournal();
    }

    protected final FakeSignalRepository signalJournal() {
        return fakeDIContainer.getSignalRepository();
    }

    protected final PipelineManager pipelineManager() {
        return fakeDIContainer.getPipelineManager();
    }

    protected final SignalsFinderContext signalsFinderContext() {
        return fakeDIContainer.getSignalsFinderContext();
    }

    protected final SignalsFinder signalsFinder() {
        return fakeDIContainer.getSignalsFinder();
    }

    protected final SignalRegistry signalRegistry() {
        return fakeDIContainer.getSignalRegistry();
    }

    protected final PerformanceCalculator performanceCalculator() {
        return fakeDIContainer.getPerformanceCalculator();
    }

    protected void initTodayDateTime(String dateTime) {
        dateTimeProvider().setNow(LocalDateTime.parse(dateTime));
    }

    protected void initInstrumentDetails(InstrumentDetail... instrumentDetails) {
        datasourceStorage().initInstrumentDetails(Arrays.asList(instrumentDetails));
    }

    protected void initAggregatedTotals(List<AggregatedTotals> tradingResults) {
        datasourceStorage().initHistoryValues(tradingResults);
    }

    protected void initAggregatedTotals(AggregatedTotals... aggregatedTotalsDtos) {
        datasourceStorage().initHistoryValues(Arrays.asList(aggregatedTotalsDtos));
    }

    protected void initIntradayData(IntradayData... intradayData) {
        datasourceStorage().initDealDatas(Arrays.asList(intradayData));
    }

    protected void clearLogs() {
        loggerProvider().clearLogs();
    }


    protected EnableUpdateInstruments enableUpdateInstrumentCommandFrom(DatasourceId datasourceId, String... tickers) {
        return new EnableUpdateInstruments(datasourceId, Arrays.stream(tickers).map(Ticker::from).toList());
    }

    protected EnableUpdateInstruments disableUpdateInstrumentCommandFrom(DatasourceId datasourceId, String... tickers) {
        return new EnableUpdateInstruments(datasourceId, Arrays.stream(tickers).map(Ticker::from).toList());
    }

    protected List<Instrument> getInstruments(DatasourceId datasourceId) {
        return datasourceRepository()
            .findBy(datasourceId)
            .map(Datasource::getInstruments)
            .orElse(new ArrayList<>());
    }

    protected List<Ticker> getTickers(DatasourceId datasourceId) {
        return getInstruments(datasourceId).stream().map(Instrument::getTicker).toList();
    }

    protected String getMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElseThrow();
    }

    protected DatasourceId getDatasourceId() {
        return datasourceRepository().getAll().get(0).getId();
    }

    protected InstrumentId getInstrumentId(String ticker) {
        return getInstruments(getDatasourceId())
            .stream()
            .filter(row -> row.getTicker().equals(Ticker.from(ticker)))
            .findFirst()
            .map(Instrument::getId)
            .orElseThrow();
    }
}
