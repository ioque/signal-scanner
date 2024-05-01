package ru.ioque.investfund.adapters.unit.rest.datasource;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.AggregatedHistoryEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.details.CurrencyPairDetailsEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.details.FuturesDetailsEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.details.IndexDetailsEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.details.StockDetailsEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.tradingstate.TradingStateEntity;
import ru.ioque.investfund.adapters.query.PsqlDatasourceQueryService;
import ru.ioque.investfund.adapters.query.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.rest.datasource.response.DatasourceResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentResponse;
import ru.ioque.investfund.adapters.unit.rest.BaseControllerTest;
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
        datasource.setInstruments(
            Set.of(
                createStock(datasource),
                createIndex(datasource),
                createFutures(datasource),
                createCurrencyPair(datasource)
            )
        );
        return datasource;
    }

    private InstrumentEntity createCurrencyPair(DatasourceEntity datasource) {
        InstrumentEntity currencyPair = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .ticker("TEST_CURRENCY_PAIR")
            .build();
        currencyPair.setDetails(
            CurrencyPairDetailsEntity.builder()
                .instrument(currencyPair)
                .name("ТЕСТОВАЯ ВАЛЮТНАЯ ПАРА")
                .lotSize(1)
                .faceUnit("RUR")
                .build()
        );
        currencyPair.setTradingState(
            new TradingStateEntity(
                currencyPair,
                LocalDateTime.now(),
                10D,
                10D,
                10D,
                1L
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
        return currencyPair;
    }

    private InstrumentEntity createFutures(DatasourceEntity datasource) {
        InstrumentEntity futures = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .ticker("TEST_FUTURES")
            .build();
        futures.setDetails(
            FuturesDetailsEntity.builder()
                .instrument(futures)
                .name("ТЕСТОВЫЙ ФЬЮЧЕРС")
                .shortName("ФЬЮЧЕРС")
                .assetCode("FR")
                .initialMargin(1D)
                .highLimit(1D)
                .lotVolume(1)
                .build()
        );
        futures.setTradingState(
            new TradingStateEntity(
                futures,
                LocalDateTime.now(),
                10D,
                10D,
                10D,
                1L
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
        return futures;
    }

    private InstrumentEntity createIndex(DatasourceEntity datasource) {
        InstrumentEntity index = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .ticker("TEST_INDEX")
            .build();
        index.setDetails(
            IndexDetailsEntity.builder()
                .instrument(index)
                .name("ТЕСТОВЫЙ ИНДЕКС")
                .shortName("ИНДЕКС")
                .annualHigh(1D)
                .annualLow(1D)
                .build()
        );
        index.setTradingState(
            new TradingStateEntity(
                index,
                LocalDateTime.now(),
                10D,
                10D,
                10D,
                1L
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
        return index;
    }

    private InstrumentEntity createStock(DatasourceEntity datasource) {
        InstrumentEntity stock = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .ticker("TEST_STOCK")
            .build();
        stock.setDetails(
            StockDetailsEntity.builder()
                .instrument(stock)
                .name("ТЕСТОВАЯ АКЦИЯ")
                .shortName("АКЦИЯ")
                .lotSize(1)
                .isin("ISIN")
                .listLevel(1)
                .regNumber("REG_NUMBER")
                .build()
        );
        stock.setTradingState(
            new TradingStateEntity(
                stock,
                LocalDateTime.now(),
                10D,
                10D,
                10D,
                1L
            )
        );
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
        return stock;
    }
}
