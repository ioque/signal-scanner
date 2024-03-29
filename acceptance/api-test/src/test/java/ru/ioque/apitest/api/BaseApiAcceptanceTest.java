package ru.ioque.apitest.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.client.exchange.ExchangeRestClient;
import ru.ioque.apitest.client.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.apitest.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.apitest.client.service.ServiceClient;
import ru.ioque.apitest.client.signalscanner.SignalScannerRestClient;
import ru.ioque.apitest.client.signalscanner.request.AddSignalScannerRequest;
import ru.ioque.apitest.client.signalscanner.response.IntradayValueResponse;
import ru.ioque.apitest.client.signalscanner.response.Signal;
import ru.ioque.apitest.client.signalscanner.response.SignalScannerInList;
import ru.ioque.apitest.client.testingsystem.TestingSystemRestClient;
import ru.ioque.apitest.client.testingsystem.response.DailyValueResponse;
import ru.ioque.apitest.dto.exchange.Exchange;
import ru.ioque.apitest.dto.exchange.Instrument;
import ru.ioque.apitest.dto.exchange.InstrumentInList;
import ru.ioque.apitest.dto.exchange.InstrumentStatistic;
import ru.ioque.apitest.fixture.InstrumentsFixture;
import ru.ioque.apitest.storage.DatasetRepository;
import ru.ioque.core.dataemulator.core.InstrumentValue;
import ru.ioque.core.tradingdatagenerator.TradingDataGeneratorFacade;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class BaseApiAcceptanceTest {
    @Autowired
    ExchangeRestClient exchangeRestClient;
    @Autowired
    TestingSystemRestClient testingSystemRestClient;
    @Autowired
    SignalScannerRestClient signalScannerRestClient;
    @Autowired
    ServiceClient serviceClient;
    @Autowired
    DatasetRepository datasetRepository;
    TradingDataGeneratorFacade generator = new TradingDataGeneratorFacade();
    InstrumentsFixture instrumentsFixture = new InstrumentsFixture();

    @BeforeEach
    void beforeEach() {
        serviceClient.clearState();
    }

    protected void initDateTime(LocalDateTime dateTime) {
        serviceClient.initDateTime(dateTime);
    }

    protected DatasetRepository datasetRepository() {
        return datasetRepository;
    }

    protected InstrumentsFixture instruments() {
        return instrumentsFixture;
    }

    protected void integrateInstruments(
        InstrumentValue... instruments
    ) {
        datasetRepository().initInstruments(Arrays.asList(instruments));
        synchronizeWithDataSource();
    }

    protected void synchronizeWithDataSource() {
        exchangeRestClient.synchronizeWithDataSource();
    }

    protected void addSignalScanner(AddSignalScannerRequest request) {
        signalScannerRestClient.saveDataScannerConfig(request);
    }

    protected void runScanning() {
        signalScannerRestClient.runScanning();
    }

    protected List<Signal> getSignalsBy(UUID id) {
        return signalScannerRestClient.getSignalScannerBy(id).getSignals();
    }

    protected List<UUID> getInstrumentIds() {
        return getInstruments()
            .stream()
            .map(InstrumentInList::getId)
            .toList();
    }

    protected List<SignalScannerInList> getSignalScanners() {
        return signalScannerRestClient.getDataScanners();
    }

    protected Exchange getExchange() {
        return exchangeRestClient.getExchange();
    }

    protected void fullIntegrate() {
        synchronizeWithDataSource();
        enableUpdateInstrumentBy(getInstrumentIds());
        integrateTradingData();
    }

    protected void clearIntradayValue() {
        exchangeRestClient.clearIntradayValue();
    }

    protected void runArchiving() {
        exchangeRestClient.runArchiving();
    }

    protected void integrateTradingData() {
        exchangeRestClient.integrateTradingData();
    }

    protected void enableUpdateInstrumentBy(List<UUID> ids) {
        exchangeRestClient.enableUpdateInstruments(new EnableUpdateInstrumentRequest(ids));
    }

    protected void disableUpdateInstrumentBy(List<UUID> ids) {
        exchangeRestClient.disableUpdateInstruments(new DisableUpdateInstrumentRequest(ids));
    }

    protected List<InstrumentInList> getInstruments() {
        return exchangeRestClient.getInstruments("");
    }

    protected List<InstrumentInList> getInstruments(Map<String, String> params) {
        return exchangeRestClient
            .getInstruments(params
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"))
            );
    }

    protected Instrument getInstrumentById(UUID id) {
        return exchangeRestClient.getInstrumentBy(id);
    }

    protected InstrumentStatistic getInstrumentStatisticBy(String ticker) {
        return exchangeRestClient
            .getInstrumentStatisticBy(
                exchangeRestClient
                    .getInstruments("")
                    .stream()
                    .filter(row -> row.getTicker().equals(ticker))
                    .map(InstrumentInList::getId)
                    .toList()
                    .get(0)
            );
    }

    protected List<IntradayValueResponse> getIntradayValues(int pageNumber, int pageSize) {
        return testingSystemRestClient.getIntradayValues(pageNumber, pageSize);
    }
    protected List<DailyValueResponse> getDailyValues(int pageNumber, int pageSize) {
        return testingSystemRestClient.getDailyValues(pageNumber, pageSize);
    }

    protected TradingDataGeneratorFacade generator() {
        return generator;
    }
}
