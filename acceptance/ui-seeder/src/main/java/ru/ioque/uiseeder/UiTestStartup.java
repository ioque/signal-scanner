package ru.ioque.uiseeder;

//@Component
//@Slf4j
//@Profile("frontend")
//@AllArgsConstructor
public class UiTestStartup //implements ApplicationListener<ApplicationReadyEvent>
{
//    ExchangeRestClient exchangeRestClient;
//    SignalScannerRestClient signalScannerRestClient;
//    ServiceClient serviceClient;
//    @Override
//    public void onApplicationEvent(final ApplicationReadyEvent event) {
//        log.info("UiTestStartup run");
//        serviceClient.clearState();
//        serviceClient.initDateTime(DefaultDataset.getLastWorkDay().atTime(LocalTime.parse("10:00:00")));
//        exchangeRestClient.synchronizeWithDataSource();
//        exchangeRestClient
//            .enableUpdateInstruments(
//                new EnableUpdateInstrumentRequest(
//                    exchangeRestClient
//                        .getInstruments("")
//                        .stream()
//                        .map(InstrumentInList::getId)
//                        .toList()
//                )
//            );
//        exchangeRestClient.integrateTradingData();
//        signalScannerRestClient.saveDataScannerConfig(DefaultDataset.getAnomalyVolumeSignalRequest(exchangeRestClient.getInstruments("")));
//        signalScannerRestClient.saveDataScannerConfig(DefaultDataset.getPrefSimpleRequest(exchangeRestClient.getInstruments("")));
//        signalScannerRestClient.saveDataScannerConfig(DefaultDataset.getCorrelationSectoralScannerRequest(exchangeRestClient.getInstruments("")));
//        signalScannerRestClient.saveDataScannerConfig(DefaultDataset.getSectoralRetardScannerRequest(exchangeRestClient.getInstruments("")));
//        signalScannerRestClient.runScanning();
//        log.info("UiTestStartup finish");
//    }
}
