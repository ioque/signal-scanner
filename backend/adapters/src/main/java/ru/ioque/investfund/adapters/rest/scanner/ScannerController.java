package ru.ioque.investfund.adapters.rest.scanner;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalScannerInListResponse;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalScannerResponse;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerLogEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ScannerLogEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ScannerController", description="Контроллер запросов к модулю \"SIGNAL-SCANNER\"")
public class ScannerController {
    SignalScannerEntityRepository signalScannerEntityRepository;
    InstrumentEntityRepository instrumentEntityRepository;
    ScannerLogEntityRepository scannerLogEntityRepository;
    @GetMapping("/api/signal-scanner")
    public List<SignalScannerInListResponse> getSignalScanners() {
        return signalScannerEntityRepository
            .findAll()
            .stream()
            .map(SignalScannerInListResponse::from)
            .toList();
    }

    @GetMapping("/api/signal-scanner/{id}")
    public SignalScannerResponse getSignalScanner(@PathVariable UUID id) {
        ScannerEntity scanner = signalScannerEntityRepository.findById(id).orElseThrow();
        List<InstrumentEntity> instruments = instrumentEntityRepository.findAllByTickerIn(scanner.getTickers());
        List<ScannerLogEntity> logs = scannerLogEntityRepository.findAllByScannerId(scanner.getId());
        return SignalScannerResponse.from(scanner, instruments, logs);
    }
}
