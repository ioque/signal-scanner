package ru.ioque.acceptance;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.exchange.ExchangeRestClient;
import ru.ioque.acceptance.adapters.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.acceptance.adapters.client.service.ServiceClient;
import ru.ioque.acceptance.adapters.client.signalscanner.SignalScannerRestClient;
import ru.ioque.acceptance.application.datasource.datasets.DefaultDataset;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;

import java.time.LocalTime;

@Component
@Profile("frontend")
@AllArgsConstructor
public class UiTestStartup implements ApplicationListener<ApplicationReadyEvent> {
    ExchangeRestClient exchangeRestClient;
    SignalScannerRestClient signalScannerRestClient;
    ServiceClient serviceClient;
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        serviceClient.clearState();
        serviceClient.initDateTime(DefaultDataset.getLastWorkDay().atTime(LocalTime.parse("10:00:00")));
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
    }
}
