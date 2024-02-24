package ru.ioque.investfund.adapters.rest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentResponse;
import ru.ioque.investfund.adapters.storage.jpa.JpaInstrumentQueryRepository;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.tradingData.Deal;
import ru.ioque.investfund.domain.exchange.value.tradingData.DealResult;
import ru.ioque.investfund.domain.exchange.value.tradingData.FuturesDeal;
import ru.ioque.investfund.domain.exchange.value.tradingData.FuturesDealResult;
import ru.ioque.investfund.domain.exchange.value.tradingData.IndexDelta;
import ru.ioque.investfund.domain.exchange.value.tradingData.IndexDeltaResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("EXCHANGE REST INTERFACE")
public class ExchangeControllerTest extends BaseControllerTest {
    @Autowired
    JpaInstrumentQueryRepository instrumentQueryRepository;

    @Test
    @SneakyThrows
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту GET /api/v1/instruments.
        """)
    public void testCase1() {
        List<Instrument> instrumentInLists = getInstruments();

        Mockito
            .when(instrumentQueryRepository.getAll())
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
        T2. Выполнение запроса по эндпоинту GET /api/v1/instruments/{id}
        """)
    public void testCase2() {
        Instrument stock = getInstruments().stream().filter(row -> row.getTicker().equals("TEST_STOCK")).findFirst().get();
        UUID id = stock.getId();

        Mockito
            .when(instrumentQueryRepository.getById(id))
            .thenReturn(stock);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/instruments/" + id))
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
