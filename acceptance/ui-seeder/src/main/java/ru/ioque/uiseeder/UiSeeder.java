package ru.ioque.uiseeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.ioque.core.dto.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.exchange.request.RegisterDatasourceRequest;
import ru.ioque.core.dto.exchange.response.InstrumentInListResponse;

import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UiSeeder implements CommandLineRunner {
    @Value("${variables.datasource_url}")
    private String datasourceUrl;
    private final ClientFacade clientFacade;

    @Override
    public void run(String... args) {
        log.info("UiTestStartup run");
        clientFacade.getDatasourceProviderClient().initDataset(UiTestsDataset.getUiTestsDataset());
        clientFacade.getServiceClient().clearState();
        clientFacade.getServiceClient().initDateTime(UiTestsDataset.getLastWorkDay().atTime(LocalTime.parse("10:00:00")));
        clientFacade.getDatasourceRestClient().registerDatasource(
            RegisterDatasourceRequest.builder()
                .name("Московская биржа")
                .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
                .url(datasourceUrl)
                .build()
        );
        clientFacade.getDatasourceRestClient().synchronizeWithDataSource();
        clientFacade.getDatasourceRestClient()
            .enableUpdateInstruments(
                new EnableUpdateInstrumentRequest(
                    clientFacade.getDatasourceRestClient()
                        .getInstruments("")
                        .stream()
                        .map(InstrumentInListResponse::getTicker)
                        .toList()
                )
            );
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(UiTestsDataset.getAnomalyVolumeSignalRequest(clientFacade.getDatasourceRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(UiTestsDataset.getPrefSimpleRequest(clientFacade.getDatasourceRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(UiTestsDataset.getCorrelationSectoralScannerRequest(clientFacade.getDatasourceRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(UiTestsDataset.getSectoralRetardScannerRequest(clientFacade.getDatasourceRestClient().getInstruments("")));
        clientFacade.getDatasourceRestClient().integrateTradingData();
        log.info("UiTestStartup finish");
    }
}
