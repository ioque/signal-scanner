package ru.ioque.investfund.adapters.rest.signalscanner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.signalscanner.request.SignalScannerRequest;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.AddScannerCommand;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignaScannerCommandController {
    ScannerManager scannerManager;
    ExchangeManager exchangeManager;

    @PostMapping("/api/v1/signal-scanner")
    public void addNewSignalProducer(@RequestBody SignalScannerRequest request) {
        scannerManager
            .addNewScanner(
                AddScannerCommand.builder()
                    .ids(request.getIds())
                    .description(request.getDescription())
                    .signalConfig(request.buildConfig())
                    .build()
            );
    }

    @PostMapping("/api/v1/signal-scanner/run")
    public void runSignalScanners() {
        scannerManager.scanning(exchangeManager.getStatistics());
    }
}
