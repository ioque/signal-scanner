package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("REPORT REPOSITORY TEST")
public class PsqlScannerLogRepositoryTest extends BaseJpaTest {
    ScannerLogRepository scannerLogRepository;

    public PsqlScannerLogRepositoryTest(
        @Autowired ScannerLogRepository scannerLogRepository
    ) {
        this.scannerLogRepository = scannerLogRepository;
    }

    @Test
    @DisplayName("T1. Сохранение списка отчетов.")
    void testCase1() {
        final UUID scannerId = UUID.randomUUID();
        scannerLogRepository.save(scannerId, new ScannerLog(LocalDateTime.now(), "test"));
        assertEquals(1, scannerLogRepository.getAllBy(scannerId).size());
    }
}
