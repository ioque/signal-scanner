package ru.ioque.investfund.adapters.rest.configurator;

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
import ru.ioque.investfund.adapters.rest.configurator.request.ScannerRequest;
import ru.ioque.investfund.application.modules.configurator.ScannerConfigurator;
import ru.ioque.investfund.application.modules.scanner.AddScannerCommand;
import ru.ioque.investfund.application.modules.scanner.UpdateScannerCommand;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="SignaScannerCommandController", description="Контроллер команд к модулю \"SIGNAL-SCANNER\"")
public class ScannerConfiguratorController {
    ScannerConfigurator scannerConfigurator;

    @PostMapping("/api/scanner")
    public void addNewScanner(@Valid @RequestBody ScannerRequest request) {
        scannerConfigurator
            .addNewScanner(
                AddScannerCommand.builder()
                    .tickers(request.getTickers())
                    .description(request.getDescription())
                    .workPeriodInMinutes(request.getWorkPeriodInMinutes())
                    .algorithmConfig(request.buildConfig())
                    .build()
            );
    }

    @PatchMapping("/api/scanner/{scannerId}")
    public void updateScanner(@PathVariable UUID scannerId, @Valid @RequestBody ScannerRequest request) {
        scannerConfigurator
            .updateScanner(
                UpdateScannerCommand.builder()
                    .id(scannerId)
                    .tickers(request.getTickers())
                    .description(request.getDescription())
                    .workPeriodInMinutes(request.getWorkPeriodInMinutes())
                    .algorithmConfig(request.buildConfig())
                    .build()
            );
    }
}
