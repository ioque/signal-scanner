package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.scanner.algorithms.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.entity.SignalScannerBot;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JPA_SIGNAL_SCANNER_REPO")
public class JpaScannerRepoTest extends BaseJpaTest {
    ExchangeRepository exchangeRepository;
    JpaScannerRepo dataJpaScannerRepo;

    private static final UUID SCANNER_ID = UUID.randomUUID();
    private static final UUID EXCHANGE_ID = UUID.randomUUID();
    private static final UUID AFKS_ID = UUID.randomUUID();

    public JpaScannerRepoTest(
        @Autowired JpaScannerRepo dataJpaScannerRepo,
        @Autowired ExchangeRepository exchangeRepository
    ) {
        this.dataJpaScannerRepo = dataJpaScannerRepo;
        this.exchangeRepository = exchangeRepository;
    }

    @Test
    @DisplayName("""
        T1. Сохранение сканера сигналов в постоянное хранилище через JPA.
        В постоянном хранилище инструментов есть данные по инструменту.
        """)
    void test1() {
        prepareExchange();

        dataJpaScannerRepo.save(SignalScannerBot
            .builder()
            .id(SCANNER_ID)
            .description("description")
            .config(new AnomalyVolumeSignalConfig(List.of(AFKS_ID), 1.5, 180, "IMOEX"))
            .build());

        assertTrue(dataJpaScannerRepo.getBy(SCANNER_ID).isPresent());
        assertTrue(dataJpaScannerRepo.getBy(SCANNER_ID).get().getSignals().isEmpty());
        assertEquals(1, dataJpaScannerRepo.getBy(SCANNER_ID).get().getFinInstruments().size());
        assertEquals("AFKS", dataJpaScannerRepo.getBy(SCANNER_ID).get().getFinInstruments().get(0).getTicker());
        assertEquals(AnomalyVolumeSignalConfig.class, dataJpaScannerRepo.getBy(SCANNER_ID).get().getConfig().getClass());
        assertEquals(180, ((AnomalyVolumeSignalConfig) dataJpaScannerRepo.getBy(SCANNER_ID).get().getConfig()).getHistoryPeriod());
        assertEquals("IMOEX", ((AnomalyVolumeSignalConfig) dataJpaScannerRepo.getBy(SCANNER_ID).get().getConfig()).getIndexTicker());
        assertEquals(1.5, ((AnomalyVolumeSignalConfig) dataJpaScannerRepo.getBy(SCANNER_ID).get().getConfig()).getScaleCoefficient());
    }

    private void prepareExchange() {
        exchangeRepository.save(getExchange());
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
