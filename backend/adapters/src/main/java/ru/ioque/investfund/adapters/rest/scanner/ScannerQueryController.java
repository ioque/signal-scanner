package ru.ioque.investfund.adapters.rest.scanner;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.psql.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.psql.dao.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaScannerRepository;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalScannerInListResponse;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalScannerResponse;
import ru.ioque.investfund.application.modules.pipeline.subscriber.SignalRegistryContext;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ScannerQueryController", description="Контроллер запросов к модулю \"SCANNER\"")
public class ScannerQueryController {
    JpaScannerRepository signalScannerEntityRepository;
    JpaInstrumentRepository instrumentEntityRepository;
    SignalRegistryContext signalRegistryContext;

    @GetMapping("/api/signals")
    public List<Signal> getAllSignals() {
        return signalScannerEntityRepository
            .findAll()
            .stream()
            .map(scannerEntity -> signalRegistryContext.getSignalsBy(ScannerId.from(scannerEntity.getId())))
            .flatMap(Collection::stream)
            .toList();
    }

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
        ScannerEntity scanner = signalScannerEntityRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("Сканер[id=%s] не существует.", id))
            );
        List<InstrumentEntity> instruments = instrumentEntityRepository.findAllByIdIn(scanner.getInstrumentIds());
        return SignalScannerResponse.of(scanner, instruments);
    }
}
