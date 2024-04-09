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
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerLogEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaScannerLogRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalScannerRepository;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ScannerQueryController", description="Контроллер запросов к модулю \"SCANNER\"")
public class ScannerQueryController {
    JpaSignalScannerRepository signalScannerEntityRepository;
    JpaInstrumentRepository instrumentEntityRepository;
    JpaScannerLogRepository jpaScannerLogRepository;

    @GetMapping("/api/scanner")
    public List<SignalScannerInListResponse> getSignalScanners() {
        return signalScannerEntityRepository
            .findAll()
            .stream()
            .map(SignalScannerInListResponse::from)
            .toList();
    }

    @GetMapping("/api/scanner/{id}")
    public SignalScannerResponse getSignalScanner(@PathVariable UUID id) {
        ScannerEntity scanner = signalScannerEntityRepository.findById(id).orElseThrow();
        List<InstrumentEntity> instruments = instrumentEntityRepository.findAllByTickerIn(scanner.getTickers());
        List<ScannerLogEntity> logs = jpaScannerLogRepository.findAllByScannerId(scanner.getId());
        return SignalScannerResponse.from(scanner, instruments, logs);
    }
}
