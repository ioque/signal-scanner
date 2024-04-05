package ru.ioque.apitest.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.ClientFacade;
import ru.ioque.apitest.DatasetManager;
import ru.ioque.core.client.exchange.ExchangeRestClient;
import ru.ioque.core.client.service.ServiceClient;
import ru.ioque.core.client.signalscanner.SignalScannerRestClient;
import ru.ioque.core.datagenerator.TradingDataGeneratorFacade;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dto.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.core.dto.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.exchange.request.RegisterDatasourceRequest;
import ru.ioque.core.dto.exchange.response.ExchangeResponse;
import ru.ioque.core.dto.exchange.response.InstrumentInListResponse;
import ru.ioque.core.dto.exchange.response.InstrumentResponse;
import ru.ioque.core.dto.exchange.response.IntradayValueResponse;
import ru.ioque.core.dto.scanner.request.AddSignalScannerRequest;
import ru.ioque.core.dto.scanner.response.SignalResponse;
import ru.ioque.core.dto.scanner.response.SignalScannerInListResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class BaseApiAcceptanceTest {
    @Value("${variables.datasource_url}")
    protected String datasourceHost;
    @Autowired
    private ClientFacade clientFacade;
    @Autowired
    private DatasetManager datasetManager;
    TradingDataGeneratorFacade generator = new TradingDataGeneratorFacade();
    @BeforeEach
    void beforeEach() {
        serviceClient().clearState();
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
    protected void registerDatasource(RegisterDatasourceRequest request) {
        exchangeClient().registerDatasource(request);
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
        integrateInstruments();
        integrateTradingData();
    }

    protected void integrateInstruments() {
        exchangeClient().synchronizeWithDataSource();
        enableUpdateInstrumentBy(getInstrumentIds());
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
        return clientFacade.getArchiveRestClient().getIntradayValues(pageNumber, pageSize);
    }
    protected TradingDataGeneratorFacade generator() {
        return generator;
    }

    protected void initDataset(Dataset dataset) {
        datasetManager.initDataset(dataset);
    }
}
