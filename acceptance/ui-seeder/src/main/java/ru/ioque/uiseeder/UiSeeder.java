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
        clientFacade.getDatasourceClient().initDataset(UiTestsDataset.getUiTestsDataset());
        clientFacade.getServiceClient().clearState();
        clientFacade.getServiceClient().initDateTime(UiTestsDataset.getLastWorkDay().atTime(LocalTime.parse("10:00:00")));
        clientFacade.getExchangeRestClient().registerDatasource(
            RegisterDatasourceRequest.builder()
                .name("Московская биржа")
                .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
                .url(datasourceUrl)
                .build()
        );
        clientFacade.getExchangeRestClient().synchronizeWithDataSource();
        clientFacade.getExchangeRestClient()
            .enableUpdateInstruments(
                new EnableUpdateInstrumentRequest(
                    clientFacade.getExchangeRestClient()
                        .getInstruments("")
                        .stream()
                        .map(InstrumentInListResponse::getId)
                        .toList()
                )
            );
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(UiTestsDataset.getAnomalyVolumeSignalRequest(clientFacade.getExchangeRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(UiTestsDataset.getPrefSimpleRequest(clientFacade.getExchangeRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(UiTestsDataset.getCorrelationSectoralScannerRequest(clientFacade.getExchangeRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(UiTestsDataset.getSectoralRetardScannerRequest(clientFacade.getExchangeRestClient().getInstruments("")));
        clientFacade.getExchangeRestClient().integrateTradingData();
        log.info("UiTestStartup finish");
    }
}
