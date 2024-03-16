package ru.ioque.investfund.adapters.rest.signalscanner;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.signalscanner.request.ScannerConfigRequest;
import ru.ioque.investfund.application.modules.scanner.AddScannerCommand;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.application.modules.scanner.UpdateScannerCommand;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="SignaScannerCommandController", description="Контроллер команд к модулю \"SIGNAL-SCANNER\"")
public class SignaScannerCommandController {
    ScannerManager scannerManager;

    @PostMapping("/api/v1/signal-scanner")
    public void addNewSignalScanner(@Valid @RequestBody ScannerConfigRequest request) {
        scannerManager
            .addNewScanner(
                AddScannerCommand.builder()
                    .signalConfig(request.buildConfig())
                    .build()
            );
    }

    @PatchMapping("/api/v1/signal-scanner/{id}")
    public void updateSignalScannerInfo(@PathVariable("id") UUID id, @Valid @RequestBody ScannerConfigRequest request) {
        scannerManager
            .updateScanner(
                UpdateScannerCommand.builder()
                    .id(id)
                    .signalConfig(request.buildConfig())
                    .build()
            );
    }

    @PostMapping("/api/v1/signal-scanner/run")
    public void runSignalScanners() {
        scannerManager.execute();
    }
}
