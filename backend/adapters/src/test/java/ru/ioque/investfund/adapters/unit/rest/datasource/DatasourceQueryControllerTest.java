package ru.ioque.investfund.adapters.unit.rest.datasource;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.AggregatedHistoryEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.CurrencyPairEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.FuturesEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.IndexEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.StockEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.TradingStateEmbeddable;
import ru.ioque.investfund.adapters.query.PsqlDatasourceQueryService;
import ru.ioque.investfund.adapters.query.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.rest.datasource.response.DatasourceResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentResponse;
import ru.ioque.investfund.adapters.unit.rest.BaseControllerTest;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DATASOURCE QUERY CONTROLLER TEST")
public class DatasourceQueryControllerTest extends BaseControllerTest {
    @Autowired
    PsqlDatasourceQueryService psqlDatasourceQueryService;
    @Autowired
    DateTimeProvider dateTimeProvider;
    private static final UUID DATASOURCE_ID = UUID.randomUUID();

    @Test
    @SneakyThrows
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту GET /api/datasource/{datasourceId}.
        """)
    public void testCase1() {
        final DatasourceEntity datasource = DatasourceEntity.builder()
            .id(DATASOURCE_ID)
            .name("EXCHANGE")
            .url("http://datasource.ru")
            .description("desc")
            .instruments(Set.of())
            .build();
        Mockito
            .when(psqlDatasourceQueryService.findDatasourceBy(DATASOURCE_ID))
            .thenReturn(datasource);
        mvc
            .perform(MockMvcRequestBuilders.get("/api/datasource/" + DATASOURCE_ID))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                DatasourceResponse.builder()
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
        T2. Выполнение запроса по эндпоинту GET /api/datasource/{datasourceId}/instruments.
        """)
    public void testCase2() {
        List<InstrumentEntity> instrumentInLists = datasource().getInstruments().stream().toList();

        Mockito
            .when(psqlDatasourceQueryService.findInstruments(new InstrumentFilterParams(
                DATASOURCE_ID,
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
            .perform(MockMvcRequestBuilders.get("/api/datasource/" + DATASOURCE_ID + "/instrument"))
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
        T3. Выполнение запроса по эндпоинту GET /api/datasource/{datasourceId}/instrument/{instrumentId}.
        """)
    public void testCase3() {
        LocalDate date = LocalDate.parse("2024-01-12");
        InstrumentEntity stock = datasource().getInstruments().stream().findFirst().orElseThrow();

        Mockito
            .when(dateTimeProvider.nowDate())
            .thenReturn(date);
        Mockito
            .when(psqlDatasourceQueryService.findInstrumentBy(DATASOURCE_ID,"TEST_STOCK"))
            .thenReturn(stock);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/datasource/" + DATASOURCE_ID + "/instrument/TEST_STOCK"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                InstrumentResponse.from(stock)
                            )
                    )
            );
    }

    private DatasourceEntity datasource() {
        DatasourceEntity datasource = DatasourceEntity.builder()
            .id(DATASOURCE_ID)
            .description("EXCHANGE")
            .name("EXCHANGE")
            .url("http://datasource.ru")
            .build();
        TradingStateEmbeddable tradingState = new TradingStateEmbeddable(
            LocalDate.now(),
            LocalTime.now(),
            10D,
            10D,
            10D,
            1L
        );
        StockEntity stock = StockEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .ticker("TEST_STOCK")
            .name("ТЕСТОВАЯ АКЦИЯ")
            .shortName("АКЦИЯ")
            .lotSize(1)
            .isin("ISIN")
            .listLevel(1)
            .regNumber("REG_NUMBER")
            .tradingState(tradingState)
            .build();
        IndexEntity index = IndexEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .name("ТЕСТОВЫЙ ИНДЕКС")
            .shortName("ИНДЕКС")
            .ticker("TEST_INDEX")
            .annualHigh(1D)
            .annualLow(1D)
            .tradingState(tradingState)
            .build();
        FuturesEntity futures = FuturesEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .ticker("TEST_FUTURES")
            .name("ТЕСТОВЫЙ ФЬЮЧЕРС")
            .shortName("ФЬЮЧЕРС")
            .assetCode("FR")
            .initialMargin(1D)
            .highLimit(1D)
            .lotVolume(1)
            .tradingState(tradingState)
            .build();
        CurrencyPairEntity currencyPair = CurrencyPairEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .name("ТЕСТОВАЯ ВАЛЮТНАЯ ПАРА")
            .ticker("TEST_CURRENCY_PAIR")
            .lotSize(1)
            .faceUnit("RUR")
            .tradingState(tradingState)
            .build();
        stock.setHistory(
            List.of(
                AggregatedHistoryEntity
                    .builder()
                    .instrument(stock)
                    .date(LocalDate.now())
                    .highPrice(1D)
                    .lowPrice(1D)
                    .openPrice(1D)
                    .closePrice(1D)
                    .value(1D)
                    .waPrice(1D)
                    .build()
            )
        );
        index.setHistory(
            List.of(
                AggregatedHistoryEntity
                    .builder()
                    .instrument(index)
                    .date(LocalDate.now())
                    .highPrice(1D)
                    .lowPrice(1D)
                    .openPrice(1D)
                    .closePrice(1D)
                    .value(1D)
                    .waPrice(1D)
                    .build()
            )
        );
        futures.setHistory(
            List.of(
                AggregatedHistoryEntity
                    .builder()
                    .instrument(futures)
                    .date(LocalDate.now())
                    .highPrice(1D)
                    .lowPrice(1D)
                    .openPrice(1D)
                    .closePrice(1D)
                    .value(1D)
                    .waPrice(1D)
                    .build()
            )
        );
        currencyPair.setHistory(
            List.of(
                AggregatedHistoryEntity
                    .builder()
                    .instrument(currencyPair)
                    .date(LocalDate.now())
                    .highPrice(1D)
                    .lowPrice(1D)
                    .openPrice(1D)
                    .closePrice(1D)
                    .value(1D)
                    .waPrice(1D)
                    .build()
            )
        );
        datasource.setInstruments(Set.of(stock, index, futures, currencyPair));
        return datasource;
    }
}
