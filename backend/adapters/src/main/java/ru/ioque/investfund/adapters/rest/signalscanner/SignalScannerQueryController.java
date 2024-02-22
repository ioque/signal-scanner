package ru.ioque.investfund.adapters.rest.signalscanner;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.signalscanner.response.SignalScannerResponse;
import ru.ioque.investfund.adapters.storage.jpa.JpaScannerRepo;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="SignalScannerQueryController", description="Контроллер запросов к модулю \"SIGNAL-SCANNER\"")
public class SignalScannerQueryController {
    JpaScannerRepo dataJpaScannerRepo;

    @GetMapping("/api/v1/signal-scanner")
    public List<SignalScannerResponse> signalProducers() {
        return dataJpaScannerRepo
            .getAll()
            .stream()
            .map(SignalScannerResponse::fromDomain)
            .toList();
    }

    @GetMapping("/api/v1/signal-scanner/{id}")
    public SignalScannerResponse signalProducer(@PathVariable UUID id) {
        return dataJpaScannerRepo
            .getBy(id)
            .map(SignalScannerResponse::fromDomain)
            .orElseThrow();
    }
}
