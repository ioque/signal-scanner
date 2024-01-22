package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.scanner.financial.algorithms.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JPA_SIGNAL_SCANNER_REPO")
public class FinDataJpaScannerRepoTest extends BaseJpaTest {
    ExchangeRepository exchangeRepository;
    JpaScannerRepo dataJpaScannerRepo;

    public FinDataJpaScannerRepoTest(
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
        exchangeRepository.save(new Exchange(UUID.randomUUID(), "a", "a", "a", Set.of(), List.of(buildStockWith().build())));
        final var instrumentId = exchangeRepository.getBy(LocalDate.now()).orElseThrow().getInstruments().get(0).getId();
        List<Instrument> finInstruments = List.of(
            Stock
                .builder()
                .id(instrumentId)
                .ticker("AFKS")
                .name("ао Система")
                .build()
        );
        UUID id = UUID.randomUUID();
        final SignalScannerBot dataScanner = new SignalScannerBot(
            id,
            "Описание",
            finInstruments.stream().map(Instrument::getId).toList(),
            new AnomalyVolumeSignalConfig(1.5, 180, "IMOEX")
        );
        dataJpaScannerRepo.save(dataScanner);
        assertEquals(dataScanner, dataJpaScannerRepo.getAll().get(0));
    }
}
