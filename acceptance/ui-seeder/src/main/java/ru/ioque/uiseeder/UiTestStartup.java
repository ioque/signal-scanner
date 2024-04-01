package ru.ioque.uiseeder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class UiTestStartup implements CommandLineRunner {
    ClientFacade clientFacade;

    @Override
    public void run(String... args) {
        log.info("UiTestStartup run");
        clientFacade.getServiceClient().clearState();
        clientFacade.getServiceClient().initDateTime(DefaultDataset.getLastWorkDay().atTime(LocalTime.parse("10:00:00")));
        exchangeRestClient.synchronizeWithDataSource();
        exchangeRestClient
            .enableUpdateInstruments(
                new EnableUpdateInstrumentRequest(
                    exchangeRestClient
                        .getInstruments("")
                        .stream()
                        .map(InstrumentInList::getId)
                        .toList()
                )
            );
        exchangeRestClient.integrateTradingData();
        signalScannerRestClient.saveDataScannerConfig(DefaultDataset.getAnomalyVolumeSignalRequest(exchangeRestClient.getInstruments("")));
        signalScannerRestClient.saveDataScannerConfig(DefaultDataset.getPrefSimpleRequest(exchangeRestClient.getInstruments("")));
        signalScannerRestClient.saveDataScannerConfig(DefaultDataset.getCorrelationSectoralScannerRequest(exchangeRestClient.getInstruments("")));
        signalScannerRestClient.saveDataScannerConfig(DefaultDataset.getSectoralRetardScannerRequest(exchangeRestClient.getInstruments("")));
        signalScannerRestClient.runScanning();
        log.info("UiTestStartup finish");
    }
}
