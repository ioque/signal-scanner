package ru.ioque.apitest;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.client.ClientFacade;
import ru.ioque.apitest.dataset.DatasetManager;
import ru.ioque.apitest.kafka.KafkaConsumer;
import ru.ioque.core.client.datasource.DatasourceHttpClient;
import ru.ioque.core.client.service.ServiceHttpClient;
import ru.ioque.core.client.signalscanner.ScannerHttpClient;
import ru.ioque.core.datagenerator.TradingDataGeneratorFacade;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dto.datasource.request.DisableUpdateInstrumentRequest;
import ru.ioque.core.dto.datasource.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.datasource.response.DatasourceResponse;
import ru.ioque.core.dto.datasource.response.InstrumentInListResponse;
import ru.ioque.core.dto.datasource.response.InstrumentResponse;
import ru.ioque.core.dto.datasource.response.IntradayResponse;
import ru.ioque.core.dto.scanner.request.CreateScannerRequest;
import ru.ioque.core.dto.scanner.response.SignalResponse;
import ru.ioque.core.dto.scanner.response.SignalScannerInListResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public abstract class DatasourceEmulatedTest {
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
        while (!kafkaConsumer.containsTradingDataIntegratedEvent()) {
            if (System.currentTimeMillis() - start > 1000) {
                return false;
            }
        }
        return true;
    }

    protected boolean waitSignalRegisteredEvent() {
        long start = System.currentTimeMillis();
        while (!kafkaConsumer.containsSignalRegisteredEvent()) {
            if (System.currentTimeMillis() - start > 1000) {
                return false;
            }
        }
        return true;
    }

    protected DatasourceHttpClient datasourceClient() {
        return clientFacade.getDatasourceRestClient();
    }

    protected ScannerHttpClient signalScannerClient() {
        return clientFacade.getSignalScannerRestClient();
    }

    private ServiceHttpClient serviceClient() {
        return clientFacade.getServiceClient();
    }

    protected void initDateTime(LocalDateTime dateTime) {
        serviceClient().initDateTime(dateTime);
    }

    protected List<DatasourceResponse> getAllDatasource() {
        return datasourceClient().getDatasourceList();
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

    protected DatasourceResponse getDatasourceBy(UUID exchangeId) {
        return datasourceClient().getDatasourceBy(exchangeId);
    }

    protected void fullIntegrate(UUID datasourceId) {
        integrateAllInstrumentFrom(datasourceId);;
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));
        integrateTradingData(datasourceId);
    }

    protected void integrateAllInstrumentFrom(UUID datasourceId) {
        datasourceClient().integrateInstruments(datasourceId);
    }

    protected void enableUpdateInstrumentBy(UUID exchangeId, List<String> tickers) {
        datasourceClient().enableUpdateInstruments(exchangeId, new EnableUpdateInstrumentRequest(tickers));
    }

    protected void disableUpdateInstrumentBy(UUID exchangeId, List<String> tickers) {
        datasourceClient().disableUpdateInstruments(exchangeId, new DisableUpdateInstrumentRequest(tickers));
    }

    protected void integrateAggregatedHistory(UUID datasourceId) {
        datasourceClient().integrateTradingData(datasourceId);
    }

    protected void integrateTradingData(UUID datasourceId) {
        datasourceClient().integrateTradingData(datasourceId);
    }

    protected List<InstrumentInListResponse> getInstruments(UUID exchangeId) {
        return datasourceClient().getInstruments(exchangeId, "");
    }

    protected List<InstrumentInListResponse> getInstruments(UUID exchangeId, Map<String, String> params) {
        return datasourceClient()
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
        return datasourceClient().getInstrumentBy(exchangeId, ticker);
    }

    protected List<IntradayResponse> getIntradayValues(int pageNumber, int pageSize) {
        return clientFacade.getArchiveRestClient().getIntradayValues(pageNumber, pageSize);
    }

    protected TradingDataGeneratorFacade generator() {
        return generator;
    }

    protected void initDataset(Dataset dataset) {
        datasetManager.initDataset(dataset);
    }
}
