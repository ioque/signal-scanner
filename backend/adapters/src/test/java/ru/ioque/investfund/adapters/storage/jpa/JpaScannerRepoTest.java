package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.domain.configurator.AnomalyVolumeAlgorithmConfig;
import ru.ioque.investfund.domain.scanner.entity.AnomalyVolumeAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANNER REPOSITORY")
public class JpaScannerRepoTest extends BaseJpaTest {
    DatasourceRepository datasourceRepository;
    FinInstrumentRepository finInstrumentRepository;
    JpaScannerRepo dataJpaScannerRepo;

    private static final UUID SCANNER_ID = UUID.randomUUID();

    public JpaScannerRepoTest(
        @Autowired JpaScannerRepo dataJpaScannerRepo,
        @Autowired FinInstrumentRepository finInstrumentRepository,
        @Autowired DatasourceRepository datasourceRepository
    ) {
        this.dataJpaScannerRepo = dataJpaScannerRepo;
        this.finInstrumentRepository = finInstrumentRepository;
        this.datasourceRepository = datasourceRepository;
    }

    @Test
    @DisplayName("""
        T1. Сохранение сканера сигналов в постоянное хранилище.
        В постоянном хранилище инструментов есть данные по инструменту.
        """)
    void test1() {
        prepareExchange(List.of(buildAfks().build()));
        LocalDateTime now = LocalDateTime.now();

        dataJpaScannerRepo.save(SignalScanner
            .builder()
            .id(SCANNER_ID)
            .workPeriodInMinutes(1)
            .description("description")
            .signals(List.of(new Signal(now, "AFKS", true)))
            .lastExecutionDateTime(now)
            .algorithm(new AnomalyVolumeAlgorithmConfig(1.5, 180, "IMOEX").factoryAlgorithm())
            .tradingSnapshots(finInstrumentRepository.getBy(List.of("AFKS")))
            .build());

        Optional<SignalScanner> scanner = dataJpaScannerRepo.getBy(SCANNER_ID);
        assertTrue(scanner.isPresent());
        assertEquals(1, scanner.get().getSignals().size());
        assertEquals(now, scanner.get().getSignals().get(0).getDateTime());
        assertEquals("AFKS", scanner.get().getSignals().get(0).getTicker());
        assertTrue(scanner.get().getLastExecutionDateTime().isPresent());
        assertEquals(now, scanner.get().getLastExecutionDateTime().get());
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
}
