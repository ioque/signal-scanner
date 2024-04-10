package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.domain.scanner.value.ScannerLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("REPORT REPOSITORY TEST")
public class ImplScannerLogRepositoryTest extends BaseJpaTest {
    ScannerLogRepository scannerLogRepository;

    public ImplScannerLogRepositoryTest(
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
