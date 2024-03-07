package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerLogEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ScannerLogEntityRepository;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.domain.scanner.financial.entity.ScannerLog;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JpaScannerLogRepository implements ScannerLogRepository {
    ScannerLogEntityRepository repository;

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