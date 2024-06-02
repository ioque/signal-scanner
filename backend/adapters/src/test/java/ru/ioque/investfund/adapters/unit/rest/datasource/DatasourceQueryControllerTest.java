package ru.ioque.investfund.adapters.unit.rest.datasource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.psql.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details.CurrencyPairDetailsEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details.FuturesDetailsEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details.IndexDetailsEntity;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details.StockDetailsEntity;
import ru.ioque.investfund.adapters.rest.Pagination;
import ru.ioque.investfund.adapters.rest.datasource.response.DatasourceResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentResponse;
import ru.ioque.investfund.adapters.service.view.PsqlDatasourceViewService;
import ru.ioque.investfund.adapters.service.view.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.unit.rest.BaseControllerTest;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DATASOURCE QUERY CONTROLLER TEST")
public class DatasourceQueryControllerTest extends BaseControllerTest {

    @Autowired
    PsqlDatasourceViewService psqlDatasourceViewService;
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
            .when(psqlDatasourceViewService.findDatasourceBy(DATASOURCE_ID))
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
            .when(psqlDatasourceViewService.getPagination(new InstrumentFilterParams(
                DATASOURCE_ID,
                null,
                null,
                null,
                0,
                100,
                "ASC",
                "ticker"
            )))
            .thenReturn(
                new Pagination<>(
                    0,
                    1,
                    4,
                    instrumentInLists
                )
            );

        mvc
            .perform(MockMvcRequestBuilders.get("/api/datasource/" + DATASOURCE_ID + "/instrument"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                new Pagination<>(
                                    0,
                                    1,
                                    4,
                                    instrumentInLists
                                        .stream()
                                        .map(InstrumentInListResponse::from)
                                        .toList()
                                )
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
            .when(psqlDatasourceViewService.findInstrumentBy(DATASOURCE_ID, "TEST_STOCK"))
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
            .updatable(false)
            .type(InstrumentType.CURRENCY_PAIR)
            .build();
        currencyPair.setDetails(
            CurrencyPairDetailsEntity.builder()
                .ticker("TEST_CURRENCY_PAIR")
                .instrument(currencyPair)
                .name("ТЕСТОВАЯ ВАЛЮТНАЯ ПАРА")
                .lotSize(1)
                .faceUnit("RUR")
                .build()
        );
        return currencyPair;
    }

    private InstrumentEntity createFutures(DatasourceEntity datasource) {
        InstrumentEntity futures = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .updatable(false)
            .type(InstrumentType.FUTURES)
            .build();
        futures.setDetails(
            FuturesDetailsEntity.builder()
                .ticker("TEST_FUTURES")
                .instrument(futures)
                .name("ТЕСТОВЫЙ ФЬЮЧЕРС")
                .shortName("ФЬЮЧЕРС")
                .assetCode("FR")
                .initialMargin(1D)
                .highLimit(1D)
                .lotVolume(1)
                .build()
        );
        return futures;
    }

    private InstrumentEntity createIndex(DatasourceEntity datasource) {
        InstrumentEntity index = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .updatable(false)
            .type(InstrumentType.INDEX)
            .build();
        index.setDetails(
            IndexDetailsEntity.builder()
                .ticker("TEST_INDEX")
                .instrument(index)
                .name("ТЕСТОВЫЙ ИНДЕКС")
                .shortName("ИНДЕКС")
                .annualHigh(1D)
                .annualLow(1D)
                .build()
        );
        return index;
    }

    private InstrumentEntity createStock(DatasourceEntity datasource) {
        InstrumentEntity stock = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .datasource(datasource)
            .updatable(false)
            .type(InstrumentType.STOCK)
            .build();
        stock.setDetails(
            StockDetailsEntity.builder()
                .ticker("TEST_STOCK")
                .instrument(stock)
                .name("ТЕСТОВАЯ АКЦИЯ")
                .shortName("АКЦИЯ")
                .lotSize(1)
                .isin("ISIN")
                .listLevel(1)
                .regNumber("REG_NUMBER")
                .build()
        );
        return stock;
    }
}
