package ru.ioque.investfund.adapters.unit.rest.scanner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.psql.dao.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaScannerRepository;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details.IndexDetailsEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details.StockDetailsEntity;
import ru.ioque.investfund.adapters.psql.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.psql.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.psql.entity.scanner.SignalEntity;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.scanner.response.AnomalyVolumeSignalScannerConfigResponse;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalResponse;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalScannerInListResponse;
import ru.ioque.investfund.adapters.rest.scanner.response.SignalScannerResponse;
import ru.ioque.investfund.adapters.unit.rest.BaseControllerTest;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.scanner.entity.ScannerStatus;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SCANNER QUERY REST CONTROLLER")
public class ScannerQueryControllerTest extends BaseControllerTest {

    @Autowired
    JpaScannerRepository signalScannerEntityRepository;
    @Autowired
    JpaInstrumentRepository instrumentEntityRepository;

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final UUID DATASOURCE_ID = UUID.randomUUID();
    private static final UUID SCANNER_ID = UUID.randomUUID();
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
                                List.of(
                                    SignalScannerInListResponse.builder()
                                        .id(SCANNER_ID)
                                        .status(ScannerStatus.ACTIVE)
                                        .description(signalProducers.get(0).getDescription())
                                        .lastExecutionDateTime(signalProducers.get(0).getLastExecutionDateTime())
                                        .build()
                                )
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
            .perform(MockMvcRequestBuilders.get("/api/scanner/" + SCANNER_ID))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                SignalScannerResponse.builder()
                                    .id(SCANNER_ID)
                                    .status(ScannerStatus.ACTIVE)
                                    .description(scanner.getDescription())
                                    .config(
                                        new AnomalyVolumeSignalScannerConfigResponse(
                                            1.5,
                                            180,
                                            "IMOEX"
                                        )
                                    )
                                    .workPeriodInMinutes(scanner.getWorkPeriodInMinutes())
                                    .lastExecutionDateTime(scanner.getLastExecutionDateTime())
                                    .instruments(instruments
                                        .stream()
                                        .map(InstrumentInListResponse::from)
                                        .toList()
                                    )
                                    .signals(
                                        List.of(
                                            SignalResponse.builder()
                                                .price(10D)
                                                .isBuy(true)
                                                .ticker("AFKS")
                                                .summary("summary")
                                                .dateTime(NOW)
                                                .build()
                                        )
                                    )
                                    .build()
                            )
                    )
            );
    }

    private List<InstrumentEntity> getInstruments() {
        return List.of(
            createAfks(),
            createImoex()
        );
    }

    private static InstrumentEntity createImoex() {
        final InstrumentEntity instrument = InstrumentEntity.builder()
            .id(IMOEX_ID)
            .type(InstrumentType.INDEX)
            .updatable(true)
            .build();
        instrument.setDetails(
            IndexDetailsEntity.builder()
                .ticker("IMOEX")
                .instrument(instrument)
                .shortName("IMOEX")
                .name("IMOEX")
                .annualHigh(1D)
                .annualLow(1D)
                .build()
        );
        return instrument;
    }

    private static InstrumentEntity createAfks() {
        InstrumentEntity instrument = InstrumentEntity.builder()
            .id(AFKS_ID)
            .type(InstrumentType.STOCK)
            .updatable(true)
            .updatable(true)
            .build();
        instrument.setDetails(
            StockDetailsEntity.builder()
                .ticker("AFKS")
                .instrument(instrument)
                .shortName("AFKS")
                .name("AFKS")
                .lotSize(100)
                .isin("isin")
                .listLevel(1)
                .regNumber("regNumber")
                .build()
        );
        return instrument;
    }

    private List<ScannerEntity> getSignalScanners() {
        var scanner = new AnomalyVolumeScannerEntity(
            SCANNER_ID,
            ScannerStatus.ACTIVE,
            1,
            "Описание",
            DATASOURCE_ID,
            List.of(AFKS_ID, IMOEX_ID),
            NOW,
            new ArrayList<>(),
            1.5,
            180,
            "IMOEX"
        );
        scanner.getSignals().add(SignalEntity.builder()
            .scanner(scanner)
            .price(10D)
            .isBuy(true)
            .summary("summary")
            .dateTime(NOW)
            .instrumentId(AFKS_ID)
            .build());
        return List.of(scanner);
    }
}
