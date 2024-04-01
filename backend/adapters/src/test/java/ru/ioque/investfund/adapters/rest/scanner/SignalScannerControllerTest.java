package ru.ioque.investfund.adapters.rest.scanner;

import org.junit.jupiter.api.DisplayName;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;

@DisplayName("SIGNAL SCANNER REST CONTROLLER")
public class SignalScannerControllerTest extends BaseControllerTest {
//    @Autowired
//    ScannerManager scannerManager;
//    @Autowired
//    SignalScannerEntityRepository signalScannerEntityRepository;
//    @Autowired
//    InstrumentEntityRepository instrumentEntityRepository;
//    @Autowired
//    ScannerLogEntityRepository scannerLogEntityRepository;
//
//    private static final UUID SIGNAL_PRODUCER_ID = UUID.randomUUID();
//    private static final UUID AFKS_ID = UUID.randomUUID();
//    private static final UUID IMOEX_ID = UUID.randomUUID();
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T1. Выполнение запроса по эндпоинту GET /api/signal-scanner.
//        """)
//    public void testCase1() {
//        var signalProducers = getSignalScanners();
//
//        Mockito
//            .when(signalScannerEntityRepository.findAll())
//            .thenReturn(signalProducers);
//
//        mvc
//            .perform(MockMvcRequestBuilders.get("/api/signal-scanner"))
//            .andExpect(status().isOk())
//            .andExpect(
//                content()
//                    .json(
//                        objectMapper
//                            .writeValueAsString(
//                                signalProducers
//                                    .stream()
//                                    .map(SignalScannerInListResponse::from)
//                                    .toList()
//                            )
//                    )
//            );
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T2. Выполнение запроса по эндпоинту GET /api/signal-scanner/{id}.
//        """)
//    public void testCase2() {
//        final SignalScannerEntity scanner = getSignalScanners().stream().findFirst().orElseThrow();
//        final List<InstrumentEntity> instruments = getInstruments();
//        final List<ScannerLogEntity> logs = getLogs();
//        Mockito
//            .when(signalScannerEntityRepository.findById(scanner.getId()))
//            .thenReturn(Optional.of(scanner));
//        Mockito
//            .when(
//                instrumentEntityRepository
//                    .findAllById(scanner.getObjectIds())
//            )
//            .thenReturn(instruments);
//        Mockito
//            .when(scannerLogEntityRepository.findAllByScannerId(SIGNAL_PRODUCER_ID))
//            .thenReturn(logs);
//        mvc
//            .perform(MockMvcRequestBuilders.get("/api/signal-scanner/" + SIGNAL_PRODUCER_ID))
//            .andExpect(status().isOk())
//            .andExpect(
//                content()
//                    .json(
//                        objectMapper
//                            .writeValueAsString(SignalScannerResponse.from(scanner, instruments, logs))
//                    )
//            );
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T3. Выполнение запроса по эндпоинту POST /api/signal-scanner.
//        """)
//    public void testCase33() {
//        mvc
//            .perform(
//                MockMvcRequestBuilders
//                    .post("/api/signal-scanner")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(toJson(PrefSimpleScannerRequest
//                        .builder()
//                        .workPeriodInMinutes(1)
//                        .description("Описание")
//                        .ids(List.of(UUID.randomUUID()))
//                        .spreadParam(1.0)
//                        .build()))
//                    .accept(MediaType.APPLICATION_JSON)
//            )
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T4. Выполнение запроса по эндпоинту POST /api/signal-scanner/run.
//        """)
//    public void testCase4() {
//        mvc
//            .perform(MockMvcRequestBuilders.post("/api/signal-scanner/run"))
//            .andExpect(status().isOk());
//    }
//
//    @SneakyThrows
//    public String toJson(Object object) {
//        return objectMapper.writeValueAsString(object);
//    }
//
//    private List<InstrumentEntity> getInstruments() {
//        return List.of(
//            StockEntity.builder()
//                .id(AFKS_ID)
//                .ticker("AFKS")
//                .shortName("AFKS")
//                .name("AFKS")
//                .lotSize(100)
//                .isin("isin")
//                .listLevel(1)
//                .regNumber("regNumber")
//                .updatable(true)
//                .build(),
//            IndexEntity.builder()
//                .id(IMOEX_ID)
//                .ticker("IMOEX")
//                .shortName("IMOEX")
//                .name("IMOEX")
//                .annualHigh(1D)
//                .annualLow(1D)
//                .updatable(true)
//                .build()
//        );
//    }
//
//    private List<ScannerLogEntity> getLogs() {
//        return List.of(
//            ScannerLogEntity.builder()
//                .id(1L)
//                .dateTime(LocalDateTime.now())
//                .message("msg")
//                .scannerId(SIGNAL_PRODUCER_ID)
//                .build()
//        );
//    }
//
//    private List<SignalScannerEntity> getSignalScanners() {
//        var scanner = new AnomalyVolumeScannerEntity(
//            SIGNAL_PRODUCER_ID,
//            1,
//            "Описание",
//            List.of(AFKS_ID, IMOEX_ID),
//            LocalDateTime.now(),
//            new ArrayList<>(),
//            1.5,
//            180,
//            "IMOEX"
//        );
//        scanner.getSignals().add(new SignalEntity(1L, scanner, AFKS_ID, true, LocalDateTime.now()));
//        return List.of(scanner);
//    }
}
