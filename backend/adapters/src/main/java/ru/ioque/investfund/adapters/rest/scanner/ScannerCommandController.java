package ru.ioque.investfund.adapters.rest.scanner;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ScannerCommandController", description="Контроллер команд к модулю \"SCANNER\"")
public class ScannerCommandController {
    ScannerManager scannerManager;

    @PostMapping("/api/scanner/signal")
    public void runScanning() {
        scannerManager.execute();
    }
}
