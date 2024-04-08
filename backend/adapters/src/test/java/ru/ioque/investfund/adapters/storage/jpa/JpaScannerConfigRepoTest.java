package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.ImplScannerConfigRepository;
import ru.ioque.investfund.adapters.persistence.ImplScannerRepository;
import ru.ioque.investfund.domain.configurator.AlgorithmConfig;
import ru.ioque.investfund.domain.configurator.AnomalyVolumeAlgorithmConfig;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;
import ru.ioque.investfund.domain.scanner.entity.AnomalyVolumeAlgorithm;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANNER CONFIG REPOSITORY")
public class JpaScannerConfigRepoTest extends BaseJpaTest {
    private static final UUID SCANNER_ID = UUID.randomUUID();
    ImplScannerConfigRepository scannerConfigRepository;
    ImplScannerRepository dataImplScannerRepository;

    public JpaScannerConfigRepoTest(
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
        prepareExchange(List.of(buildAfks().build()));
        scannerConfigRepository.save(createConfig(createAlgorithmConfig(1.5, 180, "IMOEX")));

        var scanner = dataImplScannerRepository.getBy(SCANNER_ID);
        assertTrue(scannerConfigRepository.existsBy(SCANNER_ID));
        assertTrue(scanner.isPresent());
        assertTrue(scanner.get().getSignals().isEmpty());
        assertEquals(1, scanner.get().getTradingSnapshots().size());
        assertEquals("AFKS", scanner.get().getTradingSnapshots().get(0).getTicker());
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
        prepareExchange(List.of(buildAfks().build()));
        scannerConfigRepository.save(createConfig(createAlgorithmConfig(1.5, 180, "IMOEX")));
        scannerConfigRepository.save(createConfig(createAlgorithmConfig(1.6, 182, "IMOEX")));

        var scanner = dataImplScannerRepository.getBy(SCANNER_ID);
        assertTrue(scanner.isPresent());
        assertTrue(scanner.get().getSignals().isEmpty());
        assertEquals(
            AnomalyVolumeAlgorithm.class,
            scanner.get().getAlgorithm().getClass()
        );
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

    private static SignalScannerConfig createConfig(AlgorithmConfig config) {
        return SignalScannerConfig.builder()
            .id(SCANNER_ID)
            .workPeriodInMinutes(1)
            .description("description")
            .tickers(List.of("AFKS"))
            .algorithmConfig(config)
            .build();
    }

    private static AnomalyVolumeAlgorithmConfig createAlgorithmConfig(
        Double scaleCoefficient,
        Integer historyPeriod,
        String indexTicker
    ) {
        return new AnomalyVolumeAlgorithmConfig(scaleCoefficient, historyPeriod, indexTicker);
    }
}
