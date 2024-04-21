package ru.ioque.apitest.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.ClientFacade;
import ru.ioque.apitest.DatasetManager;
import ru.ioque.apitest.kafka.IntegrationEvent;
import ru.ioque.apitest.kafka.KafkaConsumer;
import ru.ioque.core.client.datasource.DatasourceRestClient;
import ru.ioque.core.client.service.ServiceClient;
import ru.ioque.core.client.signalscanner.SignalScannerRestClient;
import ru.ioque.core.datagenerator.TradingDataGeneratorFacade;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dto.datasource.request.DisableUpdateInstrumentRequest;
import ru.ioque.core.dto.datasource.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.datasource.request.RegisterDatasourceRequest;
import ru.ioque.core.dto.datasource.response.DatasourceResponse;
import ru.ioque.core.dto.datasource.response.InstrumentInListResponse;
import ru.ioque.core.dto.datasource.response.InstrumentResponse;
import ru.ioque.core.dto.datasource.response.IntradayDtoResponse;
import ru.ioque.core.dto.scanner.request.CreateScannerRequest;
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
    @Autowired
    private KafkaConsumer kafkaConsumer;
    TradingDataGeneratorFacade generator = new TradingDataGeneratorFacade();

    @BeforeEach
    void beforeEach() {
        serviceClient().clearState();
        kafkaConsumer.clear();
    }

    protected boolean waitTradingDataIntegratedEvent() {
        long start = System.currentTimeMillis();
        while (kafkaConsumer.getMessages().stream().noneMatch(IntegrationEvent::isTradingDataIntegratedEvent)) {
            if (System.currentTimeMillis() - start > 1000) {
                return false;
            }
        }
        return true;
    }

    protected boolean waitSignalRegisteredEvent() {
        long start = System.currentTimeMillis();
        while (kafkaConsumer.getMessages().stream().noneMatch(IntegrationEvent::isSignalRegisteredEvent)) {
            if (System.currentTimeMillis() - start > 1000) {
                return false;
            }
        }
        return true;
    }

    private DatasourceRestClient exchangeClient() {
        return clientFacade.getDatasourceRestClient();
    }

    protected SignalScannerRestClient signalScannerClient() {
        return clientFacade.getSignalScannerRestClient();
    }

    private ServiceClient serviceClient() {
        return clientFacade.getServiceClient();
    }

    protected void initDateTime(LocalDateTime dateTime) {
        serviceClient().initDateTime(dateTime);
    }

    protected List<DatasourceResponse> getAllDatasource() {
        return exchangeClient().getExchanges();
    }

    protected void registerDatasource(RegisterDatasourceRequest request) {
        exchangeClient().registerDatasource(request);
    }

    protected void createScanner(CreateScannerRequest request) {
        signalScannerClient().createScanner(request);
    }

    protected void updateScanner(CreateScannerRequest request) {
        signalScannerClient().createScanner(request);
    }

    protected List<SignalResponse> getSignalsBy(UUID id) {
        return signalScannerClient().getSignalScannerBy(id).getSignals();
    }

    protected List<String> getTickers(UUID datasourceId) {
        return getInstruments(datasourceId)
            .stream()
            .map(InstrumentInListResponse::getTicker)
            .toList();
    }

    protected List<SignalScannerInListResponse> getSignalScanners() {
        return signalScannerClient().getDataScanners();
    }

    protected DatasourceResponse getExchangeBy(UUID exchangeId) {
        return exchangeClient().getExchangeBy(exchangeId);
    }

    protected void fullIntegrate(UUID datasourceId) {
        integrateInstruments(datasourceId);
        integrateTradingData(datasourceId);
    }

    protected void integrateInstruments(UUID datasourceId) {
        exchangeClient().integrateInstruments(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));
    }

    protected void runArchiving() {
        exchangeClient().runArchiving();
    }

    protected void integrateTradingData(UUID datasourceId) {
        exchangeClient().integrateTradingData(datasourceId);
    }

    protected void enableUpdateInstrumentBy(UUID exchangeId, List<String> tickers) {
        exchangeClient().enableUpdateInstruments(exchangeId, new EnableUpdateInstrumentRequest(tickers));
    }

    protected void disableUpdateInstrumentBy(UUID exchangeId, List<String> tickers) {
        exchangeClient().disableUpdateInstruments(exchangeId, new DisableUpdateInstrumentRequest(tickers));
    }

    protected List<InstrumentInListResponse> getInstruments(UUID exchangeId) {
        return exchangeClient().getInstruments(exchangeId, "");
    }

    protected List<InstrumentInListResponse> getInstruments(UUID exchangeId, Map<String, String> params) {
        return exchangeClient()
            .getInstruments(
                exchangeId,
                params
                    .entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"))
            );
    }

    protected InstrumentResponse getInstrumentBy(UUID exchangeId, String ticker) {
        return exchangeClient().getInstrumentBy(exchangeId, ticker);
    }

    protected List<IntradayDtoResponse> getIntradayValues(int pageNumber, int pageSize) {
        return clientFacade.getArchiveRestClient().getIntradayValues(pageNumber, pageSize);
    }

    protected TradingDataGeneratorFacade generator() {
        return generator;
    }

    protected void initDataset(Dataset dataset) {
        datasetManager.initDataset(dataset);
    }
}
