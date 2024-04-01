package ru.ioque.investfund.adapters.rest.exchange;

import org.junit.jupiter.api.DisplayName;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;

@DisplayName("EXCHANGE REST CONTROLLER")
public class ExchangeControllerTest extends BaseControllerTest {
//    @Autowired
//    InstrumentQueryRepository instrumentQueryRepository;
//    @Autowired
//    ExchangeEntityRepository exchangeRepository;
//    @Autowired
//    DateTimeProvider dateTimeProvider;
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T1. Выполнение запроса по эндпоинту POST /api/integrate.
//        """)
//    public void testCase1() {
//        mvc
//            .perform(MockMvcRequestBuilders.post("/api/integrate"))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T2. Выполнение запроса по эндпоинту POST /api/daily-integrate.
//        """)
//    public void testCase2() {
//        mvc
//            .perform(MockMvcRequestBuilders.post("/api/daily-integrate"))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T3. Выполнение запроса по эндпоинту PATCH /api/enable-update.
//        """)
//    public void testCase3() {
//        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
//        mvc
//            .perform(MockMvcRequestBuilders
//                .patch("/api/enable-update")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(new EnableUpdateInstrumentRequest(ids)))
//            )
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T4. Выполнение запроса по эндпоинту PATCH /api/disable-update.
//        """)
//    public void testCase4() {
//        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
//        mvc
//            .perform(MockMvcRequestBuilders
//                .patch("/api/disable-update")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(new DisableUpdateInstrumentRequest(ids)))
//            )
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T5. Выполнение запроса по эндпоинту GET /api/exchange.
//        """)
//    public void testCase5() {
//        final ExchangeEntity exchange = ExchangeEntity.builder()
//            .id(UUID.randomUUID())
//            .name("EXCHANGE")
//            .url("http://exchange.ru")
//            .description("desc")
//            .instruments(List.of())
//            .build();
//        Mockito
//            .when(exchangeRepository.findAll())
//            .thenReturn(List.of(exchange));
//        mvc
//            .perform(MockMvcRequestBuilders.get("/api/exchange"))
//            .andExpect(status().isOk())
//            .andExpect(
//                content()
//                    .json(
//                        objectMapper
//                            .writeValueAsString(
//                                ExchangeResponse.builder()
//                                    .name("EXCHANGE")
//                                    .url("http://exchange.ru")
//                                    .description("desc")
//                                    .build()
//                            )
//                    )
//            );
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T6. Выполнение запроса по эндпоинту GET /api/instruments.
//        """)
//    public void testCase6() {
//        List<Instrument> instrumentInLists = getInstruments();
//
//        Mockito
//            .when(instrumentQueryRepository.getAll(new InstrumentFilterParams(
//                null,
//                null,
//                null,
//                0,
//                100,
//                "ASC",
//                "shortName"
//            )))
//            .thenReturn(instrumentInLists);
//
//        mvc
//            .perform(MockMvcRequestBuilders.get("/api/instruments"))
//            .andExpect(status().isOk())
//            .andExpect(
//                content()
//                    .json(
//                        objectMapper
//                            .writeValueAsString(
//                                instrumentInLists
//                                    .stream()
//                                    .map(InstrumentInListResponse::fromDomain)
//                                    .toList()
//                            )
//                    )
//            );
//    }
//
//    @Test
//    @SneakyThrows
//    @DisplayName("""
//        T7. Выполнение запроса по эндпоинту GET /api/instruments/{id}
//        """)
//    public void testCase7() {
//        LocalDate date = LocalDate.parse("2024-01-12");
//        Instrument stock = getInstruments()
//            .stream()
//            .filter(row -> row.getTicker().equals("TEST_STOCK"))
//            .findFirst()
//            .orElseThrow();
//
//        Mockito
//            .when(dateTimeProvider.nowDate())
//            .thenReturn(date);
//        Mockito
//            .when(instrumentQueryRepository.getWithTradingDataBy(stock.getId(), date))
//            .thenReturn(Optional.of(stock));
//
//        mvc
//            .perform(MockMvcRequestBuilders.get("/api/instruments/" + stock.getId()))
//            .andExpect(status().isOk())
//            .andExpect(
//                content()
//                    .json(
//                        objectMapper
//                            .writeValueAsString(
//                                InstrumentResponse.fromDomain(stock)
//                            )
//                    )
//            );
//    }
//
//     @Test
//     @SneakyThrows
//     @DisplayName("""
//         T8. Выполнение запроса по эндпоинту POST /api/datasource
//         """)
//     public void testCase8() {
//         mvc
//             .perform(MockMvcRequestBuilders
//                 .post("/api/datasource")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(
//                     RegisterDatasourceRequest.builder()
//                         .name("Московская биржа")
//                         .url("http://localhost:8080")
//                         .description("Московская биржа")
//                         .build()
//                 ))
//             )
//             .andExpect(status().isOk());
//     }
//
//    protected List<Instrument> getInstruments() {
//        return List.of(
//            Stock.builder()
//                .id(UUID.randomUUID())
//                .ticker("TEST_STOCK")
//                .name("ТЕСТОВАЯ АКЦИЯ")
//                .shortName("АКЦИЯ")
//                .lotSize(1)
//                .isin("ISIN")
//                .listLevel(1)
//                .regNumber("REG_NUMBER")
//                .historyValues(
//                    List.of(
//                        HistoryValue
//                            .builder()
//                            .ticker("TEST_STOCK")
//                            .tradeDate(LocalDate.now())
//                            .highPrice(1D)
//                            .lowPrice(1D)
//                            .openPrice(1D)
//                            .closePrice(1D)
//                            .value(1D)
//                            .waPrice(1D)
//                            .build()
//                    )
//                )
//                .intradayValues(
//                    List.of(
//                        Deal.builder()
//                            .ticker("TEST_STOCK")
//                            .dateTime(LocalDateTime.now())
//                            .price(1D)
//                            .qnt(1)
//                            .value(1D)
//                            .isBuy(true)
//                            .build()
//                    )
//                )
//                .build(),
//            Futures.builder()
//                .id(UUID.randomUUID())
//                .ticker("TEST_FUTURES")
//                .name("ТЕСТОВЫЙ ФЬЮЧЕРС")
//                .shortName("ФЬЮЧЕРС")
//                .assetCode("FR")
//                .initialMargin(1D)
//                .highLimit(1D)
//                .lotVolume(1)
//                .historyValues(
//                    List.of(
//                        HistoryValue.builder()
//                            .tradeDate(LocalDate.now())
//                            .ticker("TEST_FUTURES")
//                            .openPrice(1D)
//                            .closePrice(1D)
//                            .lowPrice(1D)
//                            .highPrice(1D)
//                            .value(1D)
//                            .build()
//                    )
//                )
//                .intradayValues(
//                    List.of(
//                        Contract
//                            .builder()
//                            .ticker("TEST_FUTURES")
//                            .dateTime(LocalDateTime.now())
//                            .price(1D)
//                            .qnt(1)
//                            .build()
//                    )
//                )
//                .build(),
//            Index.builder()
//                .id(UUID.randomUUID())
//                .name("ТЕСТОВЫЙ ИНДЕКС")
//                .shortName("ИНДЕКС")
//                .ticker("TEST_INDEX")
//                .annualHigh(1D)
//                .annualLow(1D)
//                .historyValues(
//                    List.of(
//                        HistoryValue
//                            .builder()
//                            .ticker("TEST_INDEX")
//                            .tradeDate(LocalDate.now())
//                            .openPrice(1D)
//                            .closePrice(1D)
//                            .lowPrice(1D)
//                            .highPrice(1D)
//                            .value(1D)
//                            .build()
//                    )
//                )
//                .intradayValues(
//                    List.of(
//                        Delta
//                            .builder()
//                            .ticker("TEST_INDEX")
//                            .dateTime(LocalDateTime.now())
//                            .price(1D)
//                            .value(1D)
//                            .build()
//                    )
//                )
//                .build(),
//            CurrencyPair.builder()
//                .id(UUID.randomUUID())
//                .name("ТЕСТОВАЯ ВАЛЮТНАЯ ПАРА")
//                .ticker("TEST_CURRENCY_PAIR")
//                .lotSize(1)
//                .faceUnit("RUR")
//                .historyValues(
//                    List.of(
//                        HistoryValue
//                            .builder()
//                            .ticker("TEST_CURRENCY_PAIR")
//                            .tradeDate(LocalDate.now())
//                            .highPrice(1D)
//                            .lowPrice(1D)
//                            .openPrice(1D)
//                            .closePrice(1D)
//                            .value(1D)
//                            .waPrice(1D)
//                            .build()
//                    )
//                )
//                .intradayValues(
//                    List.of(
//                        Deal.builder()
//                            .ticker("TEST_CURRENCY_PAIR")
//                            .dateTime(LocalDateTime.now())
//                            .price(1D)
//                            .qnt(1)
//                            .isBuy(true)
//                            .build()
//                    )
//                )
//                .build()
//        );
//    }
}
