package ru.ioque.investfund.adapters.rest.scanner;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.scanner.request.CreateScannerRequest;
import ru.ioque.investfund.adapters.rest.scanner.request.UpdateScannerRequest;
import ru.ioque.investfund.application.modules.api.CommandBus;
import ru.ioque.investfund.application.modules.scanner.command.ActivateScanner;
import ru.ioque.investfund.application.modules.scanner.command.DeactivateScanner;
import ru.ioque.investfund.application.modules.scanner.command.RemoveScanner;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;

import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ScannerCommandController", description="Контроллер команд к модулю \"SCANNER\"")
public class ScannerCommandController {
    CommandBus commandBus;

    @PostMapping("/api/scanner")
    public void addNewScanner(@Valid @RequestBody CreateScannerRequest request) {
        commandBus.execute(request.toCommand());
    }

    @PatchMapping("/api/scanner/{scannerId}")
    public void updateScanner(@PathVariable UUID scannerId, @Valid @RequestBody UpdateScannerRequest request) {
        commandBus.execute(request.toCommand(scannerId));
    }

    @DeleteMapping("/api/scanner/{scannerId}")
    public void deleteScanner(@PathVariable UUID scannerId) {
        commandBus.execute(new RemoveScanner(ScannerId.from(scannerId)));
    }

    @PatchMapping("/api/scanner/{scannerId}/activate")
    public void activateScanner(@PathVariable UUID scannerId) {
        commandBus.execute(new ActivateScanner(ScannerId.from(scannerId)));
    }

    @PatchMapping("/api/scanner/{scannerId}/deactivate")
    public void deactivateScanner(@PathVariable UUID scannerId) {
        commandBus.execute(new DeactivateScanner(ScannerId.from(scannerId)));
    }
}
