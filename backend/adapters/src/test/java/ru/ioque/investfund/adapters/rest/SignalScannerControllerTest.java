package ru.ioque.investfund.adapters.rest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.signalscanner.request.PrefSimpleRequestAdd;
import ru.ioque.investfund.adapters.rest.signalscanner.response.SignalScannerInListResponse;
import ru.ioque.investfund.adapters.rest.signalscanner.response.SignalScannerResponse;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.IndexEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.StockEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ReportEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ReportLogEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalScannerEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ReportEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SIGNAL SCANNER REST INTERFACE")
public class SignalScannerControllerTest extends BaseControllerTest {
    @Autowired
    ScannerManager scannerManager;
    @Autowired
    SignalScannerEntityRepository signalScannerEntityRepository;
    @Autowired
    InstrumentEntityRepository instrumentEntityRepository;
    @Autowired
    ReportEntityRepository reportEntityRepository;

    private static final UUID SIGNAL_PRODUCER_ID = UUID.randomUUID();
    private static final UUID AFKS_ID = UUID.randomUUID();
    private static final UUID IMOEX_ID = UUID.randomUUID();
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    @SneakyThrows
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту GET /api/v1/signal-scanner.
        """)
    public void testCase1() {
        var signalProducers = getSignalScanners();

        Mockito
            .when(signalScannerEntityRepository.findAll())
            .thenReturn(signalProducers);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/signal-scanner"))
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
        T2. Выполнение запроса по эндпоинту GET /api/v1/signal-scanner/{id}.
        """)
    public void testCase2() {
        final SignalScannerEntity scanner = getSignalScanners().stream().findFirst().orElseThrow();
        final List<InstrumentEntity> instruments = getInstruments();
        final List<ReportEntity> reports = getReports();
        final List<SignalEntity> signals = getReports().stream().map(ReportEntity::getSignals).flatMap(Collection::stream).toList();
        Mockito
            .when(signalScannerEntityRepository.findById(scanner.getId()))
            .thenReturn(Optional.of(scanner));
        Mockito
            .when(
                instrumentEntityRepository
                    .findAllById(scanner.getObjectIds())
            )
            .thenReturn(instruments);
        Mockito
            .when(reportEntityRepository.findAllByScannerId(scanner.getId()))
            .thenReturn(reports);
        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/signal-scanner/" + scanner.getId()))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(SignalScannerResponse.from(scanner, instruments, reports, signals))
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T3. Выполнение запроса по эндпоинту POST /api/v1/signal-scannerr.
        """)
    public void testCase3() {
        mvc
            .perform(
                MockMvcRequestBuilders
                    .post("/api/v1/signal-scanner")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(PrefSimpleRequestAdd
                        .builder()
                        .description("Описание")
                        .ids(List.of(UUID.randomUUID()))
                        .spreadParam(1.0)
                        .build()))
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @SneakyThrows
    public String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
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

    private List<ReportEntity> getReports() {
        return List.of(ReportEntity.builder()
            .id(1L)
            .scannerId(SIGNAL_PRODUCER_ID)
            .time(NOW)
            .logs(List.of(
                ReportLogEntity.builder()
                    .id(1L)
                    .message("msg")
                    .time(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                    .build()
            ))
            .signals(List.of(
                SignalEntity.builder()
                    .dateTime(NOW)
                    .isBuy(true)
                    .instrumentId(AFKS_ID)
                    .build()
            ))
            .build());
    }

    private List<SignalScannerEntity> getSignalScanners() {
        return List.of(
            new AnomalyVolumeScannerEntity(
                SIGNAL_PRODUCER_ID,
                "Описание",
                List.of(AFKS_ID, IMOEX_ID),
                1.5,
                180,
                "IMOEX"
            )
        );
    }
}
