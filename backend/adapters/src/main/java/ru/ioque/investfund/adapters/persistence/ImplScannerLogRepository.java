package ru.ioque.investfund.adapters.persistence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerLogEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.domain.scanner.value.ScannerLog;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ImplScannerLogRepository implements ScannerLogRepository {
    JpaScannerLogRepository repository;

    @Override
    public List<ScannerLog> getAllBy(UUID scannerId) {
        return repository
            .findAllByScannerId(scannerId)
            .stream()
            .map(row -> new ScannerLog(row.getMessage(), row.getDateTime())
            )
            .toList();
    }

    @Override
    public void saveAll(UUID scannerId, List<ScannerLog> logs) {
        repository.saveAll(
            logs.stream().map(log -> ScannerLogEntity.builder()
                .scannerId(scannerId)
                .message(log.getMessage())
                .dateTime(log.getDateTime())
                .build()).toList()
        );
    }
}
