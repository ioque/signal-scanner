package ru.ioque.uiseeder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.ioque.core.dataset.DefaultDataset;
import ru.ioque.core.dto.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.core.dto.exchange.response.InstrumentInListResponse;

import java.time.LocalTime;

@Slf4j
@Component
@AllArgsConstructor
public class UiSeeder implements CommandLineRunner {
    ClientFacade clientFacade;

    @Override
    public void run(String... args) {
        log.info("UiTestStartup run");
        clientFacade.getServiceClient().clearState();
        clientFacade.getServiceClient().initDateTime(DefaultDataset.getLastWorkDay().atTime(LocalTime.parse("10:00:00")));
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
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(DefaultDataset.getAnomalyVolumeSignalRequest(clientFacade.getExchangeRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(DefaultDataset.getPrefSimpleRequest(clientFacade.getExchangeRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(DefaultDataset.getCorrelationSectoralScannerRequest(clientFacade.getExchangeRestClient().getInstruments("")));
        clientFacade.getSignalScannerRestClient().saveDataScannerConfig(DefaultDataset.getSectoralRetardScannerRequest(clientFacade.getExchangeRestClient().getInstruments("")));
        clientFacade.getExchangeRestClient().integrateTradingData();
        log.info("UiTestStartup finish");
    }
}