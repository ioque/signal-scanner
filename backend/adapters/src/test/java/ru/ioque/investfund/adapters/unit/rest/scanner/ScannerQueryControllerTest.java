package ru.ioque.investfund.adapters.unit.rest.scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.IndexEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.StockEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SignalEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaScannerRepository;
import ru.ioque.investfund.adapters.unit.rest.BaseControllerTest;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalScannerInListResponse;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalScannerResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SCANNER QUERY REST CONTROLLER")
public class ScannerQueryControllerTest extends BaseControllerTest {
    @Autowired
    JpaScannerRepository signalScannerEntityRepository;
    @Autowired
    JpaInstrumentRepository instrumentEntityRepository;

    private static final UUID DATASOURCE_ID = UUID.randomUUID();
    private static final UUID SIGNAL_ID = UUID.randomUUID();
    private static final UUID AFKS_ID = UUID.randomUUID();
    private static final UUID IMOEX_ID = UUID.randomUUID();

    @Test
    @SneakyThrows
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту GET /api/scanner.
        """)
    public void testCase1() {
        var signalProducers = getSignalScanners();

        Mockito
            .when(signalScannerEntityRepository.findAll())
            .thenReturn(signalProducers);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/scanner"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                signalProducers
                                    .stream()
                                    .map(SignalScannerInListResponse::from)
                                    .toList()
                            )
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T2. Выполнение запроса по эндпоинту GET /api/scanner/{scannerId}.
        """)
    public void testCase2() {
        final ScannerEntity scanner = getSignalScanners().stream().findFirst().orElseThrow();
        final List<InstrumentEntity> instruments = getInstruments();
        Mockito
            .when(signalScannerEntityRepository.findById(scanner.getId()))
            .thenReturn(Optional.of(scanner));
        Mockito
            .when(
                instrumentEntityRepository
                    .findAllByIdIn(scanner.getInstrumentIds())
            )
            .thenReturn(instruments);
        mvc
            .perform(MockMvcRequestBuilders.get("/api/scanner/" + SIGNAL_ID))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(SignalScannerResponse.of(scanner, instruments))
                    )
            );
    }

    private List<InstrumentEntity> getInstruments() {
        return List.of(
            StockEntity.builder()
                .id(AFKS_ID)
                .ticker("AFKS")
                .shortName("AFKS")
                .name("AFKS")
                .lotSize(100)
                .isin("isin")
                .listLevel(1)
                .regNumber("regNumber")
                .updatable(true)
                .build(),
            IndexEntity.builder()
                .id(IMOEX_ID)
                .ticker("IMOEX")
                .shortName("IMOEX")
                .name("IMOEX")
                .annualHigh(1D)
                .annualLow(1D)
                .updatable(true)
                .build()
        );
    }

    private List<ScannerEntity> getSignalScanners() {
        var scanner = new AnomalyVolumeScannerEntity(
            SIGNAL_ID,
            1,
            "Описание",
            DATASOURCE_ID,
            List.of(AFKS_ID, IMOEX_ID),
            LocalDateTime.now(),
            new ArrayList<>(),
            1.5,
            180,
            "IMOEX"
        );
        scanner.getSignals().add(SignalEntity.builder()
                .scanner(scanner)
                .price(10D)
                .isOpen(true)
                .isBuy(true)
                .summary("summary")
                .scanner(scanner)
                .dateTime(LocalDateTime.now())
                .instrumentId(AFKS_ID)
            .build());
        return List.of(scanner);
    }
}
