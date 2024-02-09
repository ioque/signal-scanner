package ru.ioque.investfund.adapters.rest.signalscanner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.signalscanner.request.AddSignalScannerRequest;
import ru.ioque.investfund.adapters.rest.signalscanner.request.UpdateSignalScannerRequest;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.AddScannerCommand;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.application.modules.scanner.UpdateScannerCommand;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SignaScannerCommandController {
    ScannerManager scannerManager;
    ExchangeManager exchangeManager;

    @PostMapping("/api/v1/signal-scanner")
    public void addNewSignalScanner(@RequestBody AddSignalScannerRequest request) {
        scannerManager
            .addNewScanner(
                AddScannerCommand.builder()
                    .ids(request.getIds())
                    .description(request.getDescription())
                    .signalConfig(request.buildConfig())
                    .build()
            );
    }

    @PatchMapping("/api/v1/signal-scanner/{id}")
    public void updateSignalScannerInfo(@PathVariable("id") UUID id, @RequestBody UpdateSignalScannerRequest request) {
        scannerManager
            .updateScanner(
                UpdateScannerCommand.builder()
                    .id(id)
                    .ids(request.getIds())
                    .description(request.getDescription())
                    .build()
            );
    }

    @PostMapping("/api/v1/signal-scanner/run")
    public void runSignalScanners() {
        scannerManager.scanning(exchangeManager.getStatistics());
    }
}
