package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.ImplScannerConfigRepository;
import ru.ioque.investfund.adapters.persistence.ImplScannerRepository;
import ru.ioque.investfund.domain.scanner.entity.AnomalyVolumeAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANNER CONFIG REPOSITORY")
public class ScannerConfigRepoTest extends BaseJpaTest {
    ImplScannerConfigRepository scannerConfigRepository;
    ImplScannerRepository dataImplScannerRepository;

    public ScannerConfigRepoTest(
        @Autowired ImplScannerConfigRepository scannerConfigRepository,
        @Autowired ImplScannerRepository dataImplScannerRepository
    ) {
        this.scannerConfigRepository = scannerConfigRepository;
        this.dataImplScannerRepository = dataImplScannerRepository;
    }

    @Test
    @DisplayName("""
        T1. Сохранение конфигурации нового сканера.
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
        assertTrue(scanner.get().getSignals().isEmpty());
        assertEquals(3, scanner.get().getTradingSnapshots().size());
        assertTrue(List.of("TGKN", "TGKB", "IMOEX").containsAll(scanner.get().getTickers()));
        assertEquals(
            AnomalyVolumeAlgorithm.class,
            scanner.get().getAlgorithm().getClass()
        );
        assertEquals(
            180,
            ((AnomalyVolumeAlgorithm) scanner.get().getAlgorithm()).getHistoryPeriod()
        );
        assertEquals(
            "IMOEX",
            ((AnomalyVolumeAlgorithm) scanner.get().getAlgorithm()).getIndexTicker()
        );
        assertEquals(
            1.5,
            ((AnomalyVolumeAlgorithm) scanner.get().getAlgorithm()).getScaleCoefficient()
        );
    }

    @Test
    @DisplayName("""
        T2. Обновление конфигурации сканера сигналов.
        """)
    void testCase2() {
        prepareState();
        scannerConfigRepository.save(
            createConfig(
                List.of("TGKN", "TGKB", "IMOEX"),
                createAlgorithmConfig(1.5, 180, "IMOEX")
            )
        );
        scannerConfigRepository.save(
            createConfig(
                List.of("TGKN", "IMOEX"),
                createAlgorithmConfig(1.6, 182, "IMOEX")
            )
        );

        final Optional<SignalScanner> scanner = dataImplScannerRepository.getBy(SCANNER_ID);
        assertTrue(scanner.isPresent());
        assertTrue(scanner.get().getSignals().isEmpty());
        assertEquals(
            AnomalyVolumeAlgorithm.class,
            scanner.get().getAlgorithm().getClass()
        );
        assertTrue(List.of("TGKN", "IMOEX").containsAll(scanner.get().getTickers()));
        assertEquals(
            182,
            ((AnomalyVolumeAlgorithm) scanner.get().getAlgorithm()).getHistoryPeriod()
        );
        assertEquals(
            "IMOEX",
            ((AnomalyVolumeAlgorithm) scanner.get().getAlgorithm()).getIndexTicker()
        );
        assertEquals(
            1.6,
            ((AnomalyVolumeAlgorithm) scanner.get().getAlgorithm()).getScaleCoefficient()
        );
    }
}
