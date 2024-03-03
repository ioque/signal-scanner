package ru.ioque.acceptance;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.exchange.ExchangeRestClient;
import ru.ioque.acceptance.adapters.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.SignalScannerRestClient;
import ru.ioque.acceptance.adapters.client.signalscanner.request.AnomalyVolumeScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.CorrelationSectoralScannerRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.PrefSimpleRequest;
import ru.ioque.acceptance.adapters.client.signalscanner.request.SectoralRetardScannerRequest;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;

import java.util.List;

@Component
@Profile("ui-test")
@AllArgsConstructor
public class UiTestStartup implements ApplicationListener<ApplicationReadyEvent> {
    ExchangeRestClient exchangeRestClient;
    SignalScannerRestClient signalScannerRestClient;
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
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
        signalScannerRestClient.saveDataScannerConfig(
            AnomalyVolumeScannerRequest.builder()
                .scaleCoefficient(1.5)
                .description("desc")
                .historyPeriod(180)
                .indexTicker("IMOEX")
                .ids(exchangeRestClient
                    .getInstruments("")
                    .stream()
                    .filter(row -> List.of("TGKN", "TGKB").contains(row.getTicker()))
                    .map(InstrumentInList::getId)
                    .toList())
                .build()
        );
        signalScannerRestClient.saveDataScannerConfig(
            PrefSimpleRequest.builder()
                .ids(exchangeRestClient
                    .getInstruments("")
                    .stream()
                    .filter(row -> List.of("SBER", "SBERP").contains(row.getTicker()))
                    .map(InstrumentInList::getId)
                    .toList())
                .description("desc")
                .spreadParam(1.0)
                .build()
        );
        signalScannerRestClient.saveDataScannerConfig(
            SectoralRetardScannerRequest.builder()
                .ids(exchangeRestClient
                    .getInstruments("")
                    .stream()
                    .filter(row -> List.of("TATN", "ROSN", "SIBN", "LKOH").contains(row.getTicker()))
                    .map(InstrumentInList::getId)
                    .toList())
                .description("desc")
                .historyScale(0.015)
                .intradayScale(0.015)
                .build()
        );
        signalScannerRestClient.saveDataScannerConfig(
            CorrelationSectoralScannerRequest.builder()
                .ids(exchangeRestClient
                    .getInstruments("")
                    .stream()
                    .filter(row -> List.of("TATN", "ROSN", "SIBN", "LKOH", "BRF4").contains(row.getTicker()))
                    .map(InstrumentInList::getId)
                    .toList())
                .description("desc")
                .futuresTicker("BRF4")
                .futuresOvernightScale(0.015)
                .stockOvernightScale(0.015)
                .build()
        );
        signalScannerRestClient.runScanning();
    }
}
