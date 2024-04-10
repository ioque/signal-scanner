package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.ImplScannerConfigRepository;
import ru.ioque.investfund.adapters.persistence.ImplScannerRepository;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANER REPOSITORY TEST")
public class ScanerRepoTest extends BaseJpaTest {
    ImplScannerConfigRepository scannerConfigRepository;
    ImplScannerRepository dataImplScannerRepository;

    public ScanerRepoTest(
        @Autowired ImplScannerConfigRepository scannerConfigRepository,
        @Autowired ImplScannerRepository dataImplScannerRepository
    ) {
        this.scannerConfigRepository = scannerConfigRepository;
        this.dataImplScannerRepository = dataImplScannerRepository;
    }

    @Test
    @DisplayName("""
        T1. Выгрузка сканера по его идентификатору
        """)
    void testCase1() {
        prepareState();
        scannerConfigRepository.save(
            createConfig(
                List.of("TGKN", "TGKB", "IMOEX"),
                createAlgorithmConfig(1.5, 180, "IMOEX")
            )
        );

        final Optional<SignalScanner> scanner = dataImplScannerRepository.getBy(SCANNER_ID);
        assertTrue(scannerConfigRepository.existsBy(SCANNER_ID));
        assertTrue(scanner.isPresent());

        assertEquals(3, scanner.get().getTradingSnapshots().size());
        assertSnapshot(scanner.get().getTradingSnapshots().get(0));
        assertSnapshot(scanner.get().getTradingSnapshots().get(1));
        assertSnapshot(scanner.get().getTradingSnapshots().get(2));

    }

    private void assertSnapshot(TradingSnapshot snapshot) {
        assertEquals(3, snapshot.getClosePriceSeries().size());
        assertEquals(3, snapshot.getValueSeries().size());
        assertEquals(3, snapshot.getOpenPriceSeries().size());
        assertEquals(3, snapshot.getTodayValueSeries().size());
        assertEquals(3, snapshot.getTodayPriceSeries().size());
    }
}
