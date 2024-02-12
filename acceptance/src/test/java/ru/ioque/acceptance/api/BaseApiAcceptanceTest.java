package ru.ioque.acceptance.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.adapters.client.exchange.ExchangeRestClient;
import ru.ioque.acceptance.adapters.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.acceptance.adapters.client.service.ServiceClient;
import ru.ioque.acceptance.adapters.client.signalscanner.SignalScannerRestClient;
import ru.ioque.acceptance.adapters.client.signalscanner.request.AddSignalScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.response.SignalScannerInList;
import ru.ioque.acceptance.api.fixture.InstrumentsFixture;
import ru.ioque.acceptance.application.datasource.DatasetManager;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentValue;
import ru.ioque.acceptance.domain.exchange.Exchange;
import ru.ioque.acceptance.domain.exchange.Instrument;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;
import ru.ioque.acceptance.domain.exchange.InstrumentStatistic;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class BaseApiAcceptanceTest {
    @Autowired
    ExchangeRestClient exchangeRestClient;
    @Autowired
    SignalScannerRestClient signalScannerRestClient;
    @Autowired
    ServiceClient serviceClient;
    @Autowired
    DatasetManager datasetManager;
    InstrumentsFixture instrumentsFixture = new InstrumentsFixture();

    @BeforeEach
    void beforeEach() {
        serviceClient.clearState();
    }

    protected DatasetManager datasetManager() {
        return datasetManager;
    }

    protected InstrumentsFixture instruments() {
        return instrumentsFixture;
    }

    protected void integrateInstruments(
        InstrumentValue... instruments
    ) {
        datasetManager().initDataset(Arrays.asList(instruments));
        exchangeRestClient.integrateWithDataSource();
    }

    protected void addSignalScanner(AddSignalScannerRequest request) {
        signalScannerRestClient.saveDataScannerConfig(request);
    }

    protected List<UUID> getInstrumentIds() {
        return exchangeRestClient
            .getExchange()
            .getInstruments()
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
        exchangeRestClient.integrateWithDataSource();
        exchangeRestClient.enableUpdateInstruments(
            new EnableUpdateInstrumentRequest(
                exchangeRestClient
                    .getInstruments()
                    .stream()
                    .map(InstrumentInList::getId)
                    .toList())
        );
        exchangeRestClient.integrateTradingData();
    }

    protected List<Instrument> getInstruments() {
        return getInstrumentIds().stream().map(id -> exchangeRestClient.getInstrumentBy(id)).toList();
    }

    protected InstrumentStatistic getInstrumentStatisticBy(String ticker) {
        return exchangeRestClient
            .getInstrumentStatisticBy(
                exchangeRestClient
                    .getInstruments()
                    .stream()
                    .filter(row -> row.getTicker().equals(ticker))
                    .map(InstrumentInList::getId)
                    .toList()
                    .get(0)
            );
    }
}
