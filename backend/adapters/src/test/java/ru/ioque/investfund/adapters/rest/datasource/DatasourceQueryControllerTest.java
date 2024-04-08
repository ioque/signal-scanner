package ru.ioque.investfund.adapters.rest.datasource;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.CurrencyPairEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.FuturesEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.IndexEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.StockEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.ContractEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.DealEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.DeltaEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.persistence.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;
import ru.ioque.investfund.adapters.rest.DatasourceQueryService;
import ru.ioque.investfund.adapters.rest.datasource.response.ExchangeResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentResponse;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DATASOURCE QUERY CONTROLLER TEST")
public class DatasourceQueryControllerTest extends BaseControllerTest {
    @Autowired
    DatasourceQueryService datasourceQueryService;
    @Autowired
    DateTimeProvider dateTimeProvider;
    private static final UUID DATASOURCE_ID = UUID.randomUUID();
    @Test
    @SneakyThrows
    @DisplayName("""
        T5. Выполнение запроса по эндпоинту GET /api/datasource/{datasourceId}.
        """)
    public void testCase5() {
        final DatasourceEntity datasource = DatasourceEntity.builder()
            .id(DATASOURCE_ID)
            .name("EXCHANGE")
            .url("http://datasource.ru")
            .description("desc")
            .instruments(Set.of())
            .build();
        Mockito
            .when(datasourceQueryService.findDatasource())
            .thenReturn(datasource);
        mvc
            .perform(MockMvcRequestBuilders.get("/api/datasource/" + DATASOURCE_ID))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                ExchangeResponse.builder()
                                    .id(DATASOURCE_ID)
                                    .name("EXCHANGE")
                                    .url("http://datasource.ru")
                                    .description("desc")
                                    .build()
                            )
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T6. Выполнение запроса по эндпоинту GET /api/datasource/{datasourceId}/instruments.
        """)
    public void testCase6() {
        List<InstrumentEntity> instrumentInLists = findInstrumentsBy();

        Mockito
            .when(datasourceQueryService.findInstruments(new InstrumentFilterParams(
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
            .perform(MockMvcRequestBuilders.get("/api/datasource/" + DATASOURCE_ID + "/instruments"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                instrumentInLists
                                    .stream()
                                    .map(InstrumentInListResponse::from)
                                    .toList()
                            )
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T7. Выполнение запроса по эндпоинту GET /api/datasource/{datasourceId}/instruments/{instrumentId}.
        """)
    public void testCase7() {
        LocalDate date = LocalDate.parse("2024-01-12");
        LocalDateTime dateTime = date.atStartOfDay();
        InstrumentEntity stock = findInstrumentsBy()
            .stream()
            .filter(row -> row.getTicker().equals("TEST_STOCK"))
            .findFirst()
            .orElseThrow();
        List<HistoryValueEntity> history = getHistoryValues()
            .stream()
            .filter(row -> row.getTicker().equals("TEST_STOCK"))
            .toList();
        List<IntradayValueEntity> intraday = getIntradayValues()
            .stream()
            .filter(row -> row.getTicker().equals("TEST_STOCK"))
            .toList();

        Mockito
            .when(dateTimeProvider.nowDate())
            .thenReturn(date);
        Mockito
            .when(datasourceQueryService.findInstrumentBy("TEST_STOCK"))
            .thenReturn(stock);
        Mockito
            .when(datasourceQueryService.findHistory(stock, date.minusMonths(6)))
            .thenReturn(history);
        Mockito
            .when(datasourceQueryService.findIntraday(stock, dateTime))
            .thenReturn(intraday);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/datasource/" + DATASOURCE_ID + "/instruments/TEST_STOCK"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                InstrumentResponse.from(stock, history, intraday)
                            )
                    )
            );
    }

    private List<IntradayValueEntity> getIntradayValues() {
        return List.of(
            DealEntity.builder()
                .ticker("TEST_STOCK")
                .dateTime(LocalDateTime.now())
                .price(1D)
                .qnt(1)
                .value(1D)
                .isBuy(true)
                .build(),
            ContractEntity
                .builder()
                .ticker("TEST_FUTURES")
                .dateTime(LocalDateTime.now())
                .price(1D)
                .qnt(1)
                .build(),
            DeltaEntity
                .builder()
                .ticker("TEST_INDEX")
                .dateTime(LocalDateTime.now())
                .price(1D)
                .value(1D)
                .build(),
            DealEntity.builder()
                .ticker("TEST_CURRENCY_PAIR")
                .dateTime(LocalDateTime.now())
                .price(1D)
                .qnt(1)
                .isBuy(true)
                .build()
        );
    }

    private List<HistoryValueEntity> getHistoryValues() {
        return List.of(
            HistoryValueEntity
                .builder()
                .ticker("TEST_STOCK")
                .tradeDate(LocalDate.now())
                .highPrice(1D)
                .lowPrice(1D)
                .openPrice(1D)
                .closePrice(1D)
                .value(1D)
                .waPrice(1D)
                .build(),
            HistoryValueEntity.builder()
                .tradeDate(LocalDate.now())
                .ticker("TEST_FUTURES")
                .openPrice(1D)
                .closePrice(1D)
                .lowPrice(1D)
                .highPrice(1D)
                .value(1D)
                .build(),
            HistoryValueEntity
                .builder()
                .ticker("TEST_CURRENCY_PAIR")
                .tradeDate(LocalDate.now())
                .highPrice(1D)
                .lowPrice(1D)
                .openPrice(1D)
                .closePrice(1D)
                .value(1D)
                .waPrice(1D)
                .build(),
            HistoryValueEntity
                .builder()
                .ticker("TEST_INDEX")
                .tradeDate(LocalDate.now())
                .openPrice(1D)
                .closePrice(1D)
                .lowPrice(1D)
                .highPrice(1D)
                .value(1D)
                .build()
        );
    }

    protected List<InstrumentEntity> findInstrumentsBy() {
        return List.of(
            StockEntity.builder()
                .id(UUID.randomUUID())
                .ticker("TEST_STOCK")
                .name("ТЕСТОВАЯ АКЦИЯ")
                .shortName("АКЦИЯ")
                .lotSize(1)
                .isin("ISIN")
                .listLevel(1)
                .regNumber("REG_NUMBER")
                .build(),
            FuturesEntity.builder()
                .id(UUID.randomUUID())
                .ticker("TEST_FUTURES")
                .name("ТЕСТОВЫЙ ФЬЮЧЕРС")
                .shortName("ФЬЮЧЕРС")
                .assetCode("FR")
                .initialMargin(1D)
                .highLimit(1D)
                .lotVolume(1)
                .build(),
            IndexEntity.builder()
                .id(UUID.randomUUID())
                .name("ТЕСТОВЫЙ ИНДЕКС")
                .shortName("ИНДЕКС")
                .ticker("TEST_INDEX")
                .annualHigh(1D)
                .annualLow(1D)
                .build(),
            CurrencyPairEntity.builder()
                .id(UUID.randomUUID())
                .name("ТЕСТОВАЯ ВАЛЮТНАЯ ПАРА")
                .ticker("TEST_CURRENCY_PAIR")
                .lotSize(1)
                .faceUnit("RUR")
                .build()
        );
    }
}
