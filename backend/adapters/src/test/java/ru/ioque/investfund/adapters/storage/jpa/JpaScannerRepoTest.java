package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.entity.algorithms.anomalyvolume.AnomalyVolumeAlgorithm;
import ru.ioque.investfund.domain.scanner.entity.algorithms.anomalyvolume.AnomalyVolumeAlgorithmConfigurator;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JPA_SIGNAL_SCANNER_REPO")
public class JpaScannerRepoTest extends BaseJpaTest {
    DatasourceRepository datasourceRepository;
    FinInstrumentRepository finInstrumentRepository;
    JpaScannerRepo dataJpaScannerRepo;

    private static final UUID SCANNER_ID = UUID.randomUUID();
    private static final UUID EXCHANGE_ID = UUID.randomUUID();
    private static final UUID AFKS_ID = UUID.randomUUID();

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
        T1. Сохранение сканера сигналов в постоянное хранилище через JPA.
        В постоянном хранилище инструментов есть данные по инструменту.
        """)
    void test1() {
        prepareExchange();

        dataJpaScannerRepo.save(SignalScanner
            .builder()
            .id(SCANNER_ID)
            .workPeriodInMinutes(1)
            .description("description")
            .algorithm(new AnomalyVolumeAlgorithmConfigurator(1.5, 180, "IMOEX").factoryAlgorithm())
            .tradingSnapshots(finInstrumentRepository.getBy(List.of("AFKS")))
            .build());

        assertTrue(dataJpaScannerRepo.getBy(SCANNER_ID).isPresent());
        assertTrue(dataJpaScannerRepo.getBy(SCANNER_ID).get().getSignals().isEmpty());
        assertEquals(1, dataJpaScannerRepo.getBy(SCANNER_ID).get().getTradingSnapshots().size());
        assertEquals("AFKS", dataJpaScannerRepo.getBy(SCANNER_ID).get().getTradingSnapshots().get(0).getTicker());
        assertEquals(
            AnomalyVolumeAlgorithm.class,
            dataJpaScannerRepo.getBy(SCANNER_ID).get().getAlgorithm().getClass()
        );
        assertEquals(
            180,
            ((AnomalyVolumeAlgorithm) dataJpaScannerRepo.getBy(SCANNER_ID).get().getAlgorithm()).getHistoryPeriod()
        );
        assertEquals(
            "IMOEX",
            ((AnomalyVolumeAlgorithm) dataJpaScannerRepo.getBy(SCANNER_ID).get().getAlgorithm()).getIndexTicker()
        );
        assertEquals(
            1.5,
            ((AnomalyVolumeAlgorithm) dataJpaScannerRepo.getBy(SCANNER_ID).get().getAlgorithm()).getScaleCoefficient()
        );
    }

    private void prepareExchange() {
        datasourceRepository.save(getExchange());
    }

    private static Stock getAfks() {
        return Stock
            .builder()
            .id(AFKS_ID)
            .ticker("AFKS")
            .name("АФК «Система»")
            .shortName("ао Система")
            .listLevel(1)
            .build();
    }

    private Exchange getExchange() {
        return new Exchange(EXCHANGE_ID, "Московская биржа", "https://moex.com", "description", List.of(getAfks()));
    }
}
