package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JPA_REPORT_REPO")
public class JpaScannerLogRepositoryTest extends BaseJpaTest {
    ScannerLogRepository scannerLogRepository;

    public JpaScannerLogRepositoryTest(
        @Autowired ScannerLogRepository scannerLogRepository
    ) {
        this.scannerLogRepository = scannerLogRepository;
    }

    @Test
    @DisplayName("T1. Сохранение списка отчетов.")
    void testCase1() {
        final UUID scannerId = UUID.randomUUID();
        scannerLogRepository.saveAll(scannerId, List.of(new ScannerLog("test", LocalDateTime.now())));
        assertEquals(1, scannerLogRepository.getAllBy(scannerId).size());
    }
}
