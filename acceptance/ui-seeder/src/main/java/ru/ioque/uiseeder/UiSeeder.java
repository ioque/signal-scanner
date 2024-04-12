package ru.ioque.uiseeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dto.datasource.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.datasource.request.RegisterDatasourceRequest;

import java.time.LocalTime;
import java.util.UUID;

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
        Dataset uiTestsDataset = UiTestsDataset.getUiTestsDataset();
        clientFacade.getDatasourceProviderClient().initDataset(uiTestsDataset);
        clientFacade.getServiceClient().clearState();
        clientFacade.getServiceClient().initDateTime(UiTestsDataset.getLastWorkDay().atTime(LocalTime.parse("10:00:00")));
        clientFacade.getDatasourceRestClient().registerDatasource(
            RegisterDatasourceRequest.builder()
                .name("Конфигурируемый источник данных")
                .description("Конфигурируемый источник данных, использовать для тестирования алгоритмов.")
                .url(datasourceUrl)
                .build()
        );
        final UUID datasourceID = clientFacade.getDatasourceRestClient().getExchanges().get(0).getId();
        clientFacade.getDatasourceRestClient().integrateInstruments(datasourceID);
        clientFacade.getDatasourceRestClient()
            .enableUpdateInstruments(
                datasourceID,
                new EnableUpdateInstrumentRequest(uiTestsDataset
                    .getInstruments()
                    .stream()
                    .map(Instrument::getTicker)
                    .toList())
            );
        clientFacade.getSignalScannerRestClient().createScanner(UiTestsDataset.getAnomalyVolumeSignalRequest(datasourceID));
        clientFacade.getSignalScannerRestClient().createScanner(UiTestsDataset.getPrefSimpleRequest(datasourceID));
        clientFacade
            .getSignalScannerRestClient()
            .createScanner(UiTestsDataset.getCorrelationSectoralScannerRequest(datasourceID));
        clientFacade.getSignalScannerRestClient().createScanner(UiTestsDataset.getSectoralRetardScannerRequest(datasourceID));
        clientFacade.getDatasourceRestClient().integrateTradingData(datasourceID);
        log.info("UiTestStartup finish");
    }
}
