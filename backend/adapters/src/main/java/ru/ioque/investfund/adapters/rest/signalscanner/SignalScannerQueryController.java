package ru.ioque.investfund.adapters.rest.signalscanner;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.signalscanner.response.SignalScannerInListResponse;
import ru.ioque.investfund.adapters.rest.signalscanner.response.SignalScannerResponse;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ReportEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ReportEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="SignalScannerQueryController", description="Контроллер запросов к модулю \"SIGNAL-SCANNER\"")
public class SignalScannerQueryController {
    SignalScannerEntityRepository signalScannerEntityRepository;
    InstrumentEntityRepository instrumentEntityRepository;
    ReportEntityRepository reportEntityRepository;
    @GetMapping("/api/v1/signal-scanner")
    public List<SignalScannerInListResponse> getSignalScanners() {
        return signalScannerEntityRepository
            .findAll()
            .stream()
            .map(SignalScannerInListResponse::from)
            .toList();
    }

    @GetMapping("/api/v1/signal-scanner/{id}")
    public SignalScannerResponse getSignalScanner(@PathVariable UUID id) {
        SignalScannerEntity scanner = signalScannerEntityRepository.findById(id).orElseThrow();
        List<InstrumentEntity> instruments = instrumentEntityRepository.findAllById(scanner.getObjectIds());
        List<ReportEntity> reports = reportEntityRepository.findAllByScannerId(scanner.getId());
        List<SignalEntity> signals = reports.stream().map(ReportEntity::getSignals).flatMap(Collection::stream).toList();
        return SignalScannerResponse.from(scanner, instruments, reports, signals);
    }
}
