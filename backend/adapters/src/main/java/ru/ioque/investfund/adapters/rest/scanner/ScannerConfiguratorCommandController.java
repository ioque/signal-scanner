package ru.ioque.investfund.adapters.rest.scanner;

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
import ru.ioque.investfund.adapters.rest.scanner.request.CreateScannerRequest;
import ru.ioque.investfund.adapters.rest.scanner.request.UpdateScannerRequest;
import ru.ioque.investfund.application.command.CommandBus;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ScannerCommandController", description="Контроллер команд к модулю \"SCANNER\"")
public class ScannerConfiguratorCommandController {
    CommandBus commandBus;

    @PostMapping("/api/scanner")
    public void addNewScanner(@Valid @RequestBody CreateScannerRequest request) {
        commandBus.execute(request.toCommand());
    }

    @PatchMapping("/api/scanner/{scannerId}")
    public void updateScanner(@PathVariable UUID scannerId, @Valid @RequestBody UpdateScannerRequest request) {
        commandBus.execute(request.toCommand(scannerId));
    }
}
