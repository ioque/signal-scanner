package ru.ioque.investfund.adapters.rest.exchange;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;
import ru.ioque.investfund.adapters.rest.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.exchange.response.ExchangeResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentResponse;
import ru.ioque.investfund.adapters.storage.jpa.JpaInstrumentQueryRepository;
import ru.ioque.investfund.adapters.storage.jpa.filter.InstrumentFilterParams;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.Deal;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.exchange.value.FuturesDeal;
import ru.ioque.investfund.domain.exchange.value.FuturesDealResult;
import ru.ioque.investfund.domain.exchange.value.IndexDelta;
import ru.ioque.investfund.domain.exchange.value.IndexDeltaResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("EXCHANGE REST CONTROLLER")
public class ExchangeControllerTest extends BaseControllerTest {
    @Autowired
    JpaInstrumentQueryRepository instrumentQueryRepository;
    @Autowired
    ExchangeRepository exchangeRepository;
    @Autowired
    DateTimeProvider dateTimeProvider;

    @Test
    @SneakyThrows
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту POST /api/v1/integrate.
        """)
    public void testCase1() {
        mvc
            .perform(MockMvcRequestBuilders.post("/api/v1/integrate"))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T2. Выполнение запроса по эндпоинту POST /api/v1/daily-integrate.
        """)
    public void testCase2() {
        mvc
            .perform(MockMvcRequestBuilders.post("/api/v1/daily-integrate"))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T3. Выполнение запроса по эндпоинту PATCH /api/v1/enable-update.
        """)
    public void testCase3() {
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        mvc
            .perform(MockMvcRequestBuilders
                .patch("/api/v1/enable-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EnableUpdateInstrumentRequest(ids)))
            )
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T4. Выполнение запроса по эндпоинту PATCH /api/v1/disable-update.
        """)
    public void testCase4() {
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        mvc
            .perform(MockMvcRequestBuilders
                .patch("/api/v1/disable-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DisableUpdateInstrumentRequest(ids)))
            )
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T5. Выполнение запроса по эндпоинту GET /api/v1/exchange.
        """)
    public void testCase5() {
        final Exchange exchange = Exchange.builder()
            .id(UUID.randomUUID())
            .name("EXCHANGE")
            .url("http://exchange.ru")
            .description("desc")
            .instruments(List.of())
            .build();
        Mockito
            .when(exchangeRepository.get())
            .thenReturn(exchange);
        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/exchange"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                ExchangeResponse.builder()
                                    .name("EXCHANGE")
                                    .url("http://exchange.ru")
                                    .description("desc")
                                    .build()
                            )
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T6. Выполнение запроса по эндпоинту GET /api/v1/instruments.
        """)
    public void testCase6() {
        List<Instrument> instrumentInLists = getInstruments();

        Mockito
            .when(instrumentQueryRepository.getAll(new InstrumentFilterParams(
                null,
                null,
                null,
                0,
                100,
                "ASC",
                "shortName"
            )))
            .thenReturn(instrumentInLists);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/instruments"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                instrumentInLists
                                    .stream()
                                    .map(InstrumentInListResponse::fromDomain)
                                    .toList()
                            )
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T7. Выполнение запроса по эндпоинту GET /api/v1/instruments/{id}
        """)
    public void testCase7() {
        LocalDate date = LocalDate.parse("2024-01-12");
        Instrument stock = getInstruments()
            .stream()
            .filter(row -> row.getTicker().equals("TEST_STOCK"))
            .findFirst()
            .orElseThrow();

        Mockito
            .when(dateTimeProvider.nowDate())
            .thenReturn(date);
        Mockito
            .when(instrumentQueryRepository.getWithTradingDataBy(stock.getId(), date))
            .thenReturn(stock);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/instruments/" + stock.getId()))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                InstrumentResponse.fromDomain(stock)
                            )
                    )
            );
    }

    protected List<Instrument> getInstruments() {
        return List.of(
            Stock.builder()
                .id(UUID.randomUUID())
                .ticker("TEST_STOCK")
                .name("ТЕСТОВАЯ АКЦИЯ")
                .shortName("АКЦИЯ")
                .lotSize(1)
                .isin("ISIN")
                .listLevel(1)
                .regNumber("REG_NUMBER")
                .dailyValues(
                    List.of(
                        DealResult
                            .builder()
                            .ticker("TEST_STOCK")
                            .tradeDate(LocalDate.now())
                            .maxPrice(1D)
                            .minPrice(1D)
                            .openPrice(1D)
                            .closePrice(1D)
                            .value(1D)
                            .numTrades(1D)
                            .volume(1D)
                            .waPrice(1D)
                            .build()
                    )
                )
                .intradayValues(
                    List.of(
                        Deal.builder()
                            .number(1L)
                            .ticker("TEST_STOCK")
                            .dateTime(LocalDateTime.now())
                            .price(1D)
                            .qnt(1)
                            .value(1D)
                            .isBuy(true)
                            .build()
                    )
                )
                .build(),
            Futures.builder()
                .id(UUID.randomUUID())
                .ticker("TEST_FUTURES")
                .name("ТЕСТОВЫЙ ФЬЮЧЕРС")
                .shortName("ФЬЮЧЕРС")
                .assetCode("FR")
                .initialMargin(1D)
                .highLimit(1D)
                .lotVolume(1)
                .dailyValues(
                    List.of(
                        FuturesDealResult.builder()
                            .tradeDate(LocalDate.now())
                            .ticker("TEST_FUTURES")
                            .openPrice(1D)
                            .closePrice(1D)
                            .minPrice(1D)
                            .maxPrice(1D)
                            .value(1D)
                            .volume(1)
                            .openPositionValue(1D)
                            .build()
                    )
                )
                .intradayValues(
                    List.of(
                        FuturesDeal
                            .builder()
                            .number(1L)
                            .ticker("TEST_FUTURES")
                            .dateTime(LocalDateTime.now())
                            .price(1D)
                            .qnt(1)
                            .build()
                    )
                )
                .build(),
            Index.builder()
                .id(UUID.randomUUID())
                .name("ТЕСТОВЫЙ ИНДЕКС")
                .shortName("ИНДЕКС")
                .ticker("TEST_INDEX")
                .annualHigh(1D)
                .annualLow(1D)
                .dailyValues(
                    List.of(
                        IndexDeltaResult
                            .builder()
                            .ticker("TEST_INDEX")
                            .tradeDate(LocalDate.now())
                            .openPrice(1D)
                            .closePrice(1D)
                            .minPrice(1D)
                            .maxPrice(1D)
                            .value(1D)
                            .capitalization(1D)
                            .build()
                    )
                )
                .intradayValues(
                    List.of(
                        IndexDelta
                            .builder()
                            .number(1L)
                            .ticker("TEST_INDEX")
                            .dateTime(LocalDateTime.now())
                            .price(1D)
                            .value(1D)
                            .build()
                    )
                )
                .build(),
            CurrencyPair.builder()
                .id(UUID.randomUUID())
                .name("ТЕСТОВАЯ ВАЛЮТНАЯ ПАРА")
                .ticker("TEST_CURRENCY_PAIR")
                .lotSize(1)
                .faceUnit("RUR")
                .dailyValues(
                    List.of(
                        DealResult
                            .builder()
                            .ticker("TEST_CURRENCY_PAIR")
                            .tradeDate(LocalDate.now())
                            .maxPrice(1D)
                            .minPrice(1D)
                            .openPrice(1D)
                            .closePrice(1D)
                            .value(1D)
                            .numTrades(1D)
                            .volume(1D)
                            .waPrice(1D)
                            .build()
                    )
                )
                .intradayValues(
                    List.of(
                        Deal.builder()
                            .number(1L)
                            .ticker("TEST_CURRENCY_PAIR")
                            .dateTime(LocalDateTime.now())
                            .price(1D)
                            .qnt(1)
                            .isBuy(true)
                            .build()
                    )
                )
                .build()
        );
    }
}
