package ru.ioque.apitest;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.client.ClientFacade;
import ru.ioque.apitest.dataset.DatasetManager;
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
import ru.ioque.core.dto.risk.response.EmulatedPositionResponse;
import ru.ioque.core.dto.scanner.request.CreateScannerRequest;
import ru.ioque.core.dto.scanner.response.SignalResponse;
import ru.ioque.core.dto.scanner.response.SignalScannerInListResponse;
import ru.ioque.core.dto.scanner.response.SignalScannerResponse;

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
    TradingDataGeneratorFacade generator = new TradingDataGeneratorFacade();

    @BeforeEach
    void beforeEach() {
        serviceClient().clearState();
    }

    protected boolean waitOpenEmulatedPositions(int size) {
        long start = System.currentTimeMillis();
        while (getOpenEmulatedPositions().size() != size) {
            if (System.currentTimeMillis() - start > 2000) {
                return false;
            }
        }
        return true;
    }

    protected boolean waitScanningFinishedEvent(LocalDateTime lastExecutionTime) {
        long start = System.currentTimeMillis();
        while (getSignalScanners()
            .stream()
            .filter(row -> row.getLastExecutionDateTime().equals(lastExecutionTime))
            .findFirst()
            .isEmpty()
        ) {
            if (System.currentTimeMillis() - start > 2000) {
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

    protected ServiceHttpClient serviceClient() {
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


    protected void removeScanner(UUID scannerId) {
        signalScannerClient().removeScanner(scannerId);
    }

    protected void activateScanner(UUID scannerId) {
        signalScannerClient().activateScanner(scannerId);
    }

    protected void deactivateScanner(UUID scannerId) {
        signalScannerClient().deactivateScanner(scannerId);
    }

    protected List<SignalResponse> getSignalsBy(UUID id) {
        return getScannerById(id).getSignals();
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

    protected SignalScannerResponse getScannerById(UUID scannerId) {
        return signalScannerClient().getSignalScannerBy(scannerId);
    }

    protected DatasourceResponse getDatasourceBy(UUID exchangeId) {
        return datasourceClient().getDatasourceBy(exchangeId);
    }

    protected List<EmulatedPositionResponse> getOpenEmulatedPositions() {
        return clientFacade
            .getRiskManagerClient()
            .getEmulatedPositions()
            .stream()
            .filter(EmulatedPositionResponse::getIsOpen)
            .toList();
    }

    protected void fullIntegrate(UUID datasourceId) {
        synchronizeDatasource(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));
        publishIntradayData(datasourceId);
    }

    protected void synchronizeDatasource(UUID datasourceId) {
        datasourceClient().synchronizeDatasource(datasourceId);
    }

    protected void prepareDatasource(UUID datasourceId) {
        synchronizeDatasource(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));
        updateAggregatedTotals(datasourceId);
    }

    protected void updateAggregatedTotals(UUID datasourceId) {
        datasourceClient().updateAggregatedTotals(datasourceId);
    }

    protected void stopDatasource(UUID datasourceId) {
        datasourceClient().stopDatasource(datasourceId);
    }

    protected void enableUpdateInstrumentBy(UUID exchangeId, List<String> tickers) {
        datasourceClient().enableUpdateInstruments(exchangeId, new EnableUpdateInstrumentRequest(tickers));
    }

    protected void disableUpdateInstrumentBy(UUID exchangeId, List<String> tickers) {
        datasourceClient().disableUpdateInstruments(exchangeId, new DisableUpdateInstrumentRequest(tickers));
    }

    protected void publishIntradayData(UUID datasourceId) {
        datasourceClient().publishIntradayData(datasourceId);
    }

    protected List<InstrumentInListResponse> getInstruments(UUID exchangeId) {
        return datasourceClient().getInstruments(exchangeId, "").getElements();
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
            ).getElements();
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
