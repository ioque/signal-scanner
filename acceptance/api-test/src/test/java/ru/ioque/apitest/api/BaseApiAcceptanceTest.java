package ru.ioque.apitest.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.ClientFacade;
import ru.ioque.apitest.fixture.InstrumentsFixture;
import ru.ioque.apitest.repos.DatasetRepository;
import ru.ioque.core.client.exchange.ExchangeRestClient;
import ru.ioque.core.client.service.ServiceClient;
import ru.ioque.core.client.signalscanner.SignalScannerRestClient;
import ru.ioque.core.client.testingsystem.TestingSystemRestClient;
import ru.ioque.core.datagenerator.TradingDataGeneratorFacade;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.dto.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.core.dto.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.exchange.response.ExchangeResponse;
import ru.ioque.core.dto.exchange.response.HistoryValueResponse;
import ru.ioque.core.dto.exchange.response.InstrumentInListResponse;
import ru.ioque.core.dto.exchange.response.InstrumentResponse;
import ru.ioque.core.dto.exchange.response.IntradayValueResponse;
import ru.ioque.core.dto.scanner.request.AddSignalScannerRequest;
import ru.ioque.core.dto.scanner.response.SignalResponse;
import ru.ioque.core.dto.scanner.response.SignalScannerInListResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class BaseApiAcceptanceTest {
    @Autowired
    private ClientFacade clientFacade;
    @Autowired
    private DatasetRepository datasetRepository;
    TradingDataGeneratorFacade generator = new TradingDataGeneratorFacade();
    InstrumentsFixture instrumentsFixture = new InstrumentsFixture();

    @BeforeEach
    void beforeEach() {
        serviceClient().clearState();
    }
    private TestingSystemRestClient testingSystemClient() {
        return clientFacade.getTestingSystemRestClient();
    }
    private ExchangeRestClient exchangeClient() {
        return clientFacade.getExchangeRestClient();
    }
    private SignalScannerRestClient signalScannerClient() {
        return clientFacade.getSignalScannerRestClient();
    }
    private ServiceClient serviceClient() {
        return clientFacade.getServiceClient();
    }

    protected void initDateTime(LocalDateTime dateTime) {
        serviceClient().initDateTime(dateTime);
    }

    protected DatasetRepository datasetRepository() {
        return datasetRepository;
    }

    protected InstrumentsFixture instruments() {
        return instrumentsFixture;
    }

    protected void integrateInstruments(
        Instrument... instruments
    ) {
        datasetRepository().initInstruments(Arrays.asList(instruments));
        synchronizeWithDataSource();
    }

    protected void synchronizeWithDataSource() {
        exchangeClient().synchronizeWithDataSource();
    }

    protected void addSignalScanner(AddSignalScannerRequest request) {
        signalScannerClient().saveDataScannerConfig(request);
    }

    protected List<SignalResponse> getSignalsBy(UUID id) {
        return signalScannerClient().getSignalScannerBy(id).getSignals();
    }

    protected List<UUID> getInstrumentIds() {
        return getInstruments()
            .stream()
            .map(InstrumentInListResponse::getId)
            .toList();
    }

    protected List<SignalScannerInListResponse> getSignalScanners() {
        return signalScannerClient().getDataScanners();
    }

    protected ExchangeResponse getExchange() {
        return exchangeClient().getExchange();
    }

    protected void fullIntegrate() {
        synchronizeWithDataSource();
        enableUpdateInstrumentBy(getInstrumentIds());
        integrateTradingData();
    }

    protected void clearIntradayValue() {
        exchangeClient().clearIntradayValue();
    }

    protected void runArchiving() {
        exchangeClient().runArchiving();
    }

    protected void integrateTradingData() {
        exchangeClient().integrateTradingData();
    }

    protected void enableUpdateInstrumentBy(List<UUID> ids) {
        exchangeClient().enableUpdateInstruments(new EnableUpdateInstrumentRequest(ids));
    }

    protected void disableUpdateInstrumentBy(List<UUID> ids) {
        exchangeClient().disableUpdateInstruments(new DisableUpdateInstrumentRequest(ids));
    }

    protected List<InstrumentInListResponse> getInstruments() {
        return exchangeClient().getInstruments("");
    }

    protected List<InstrumentInListResponse> getInstruments(Map<String, String> params) {
        return exchangeClient()
            .getInstruments(params
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"))
            );
    }

    protected InstrumentResponse getInstrumentById(UUID id) {
        return exchangeClient().getInstrumentBy(id);
    }

    protected List<IntradayValueResponse> getIntradayValues(int pageNumber, int pageSize) {
        return testingSystemClient().getIntradayValues(pageNumber, pageSize);
    }
    protected List<HistoryValueResponse> getHistoryValues(int pageNumber, int pageSize) {
        return testingSystemClient().getHistoryValues(pageNumber, pageSize);
    }

    protected TradingDataGeneratorFacade generator() {
        return generator;
    }
}
