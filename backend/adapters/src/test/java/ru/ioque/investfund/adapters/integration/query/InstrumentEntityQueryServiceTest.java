package ru.ioque.investfund.adapters.integration.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.integration.InfrastructureTest;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.query.PsqlDatasourceQueryService;
import ru.ioque.investfund.adapters.query.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.rest.Pagination;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PSQL DATASOURCE QUERY SERVICE TEST")
public class InstrumentEntityQueryServiceTest extends InfrastructureTest {
    private static final UUID DATASOURCE_ID = UUID.randomUUID();
    PsqlDatasourceQueryService psqlDatasourceQueryService;
    DatasourceRepository datasourceRepository;
    JpaDatasourceRepository jpaDatasourceRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;
    JpaInstrumentRepository jpaInstrumentRepository;

    public InstrumentEntityQueryServiceTest(
        @Autowired PsqlDatasourceQueryService psqlDatasourceQueryService,
        @Autowired DatasourceRepository datasourceRepository,
        @Autowired JpaDatasourceRepository jpaDatasourceRepository,
        @Autowired JpaInstrumentRepository jpaInstrumentRepository,
        @Autowired JpaIntradayValueRepository jpaIntradayValueRepository
    ) {
        this.psqlDatasourceQueryService = psqlDatasourceQueryService;
        this.datasourceRepository = datasourceRepository;
        this.jpaDatasourceRepository = jpaDatasourceRepository;
        this.jpaInstrumentRepository = jpaInstrumentRepository;
        this.jpaIntradayValueRepository = jpaIntradayValueRepository;
    }

    @BeforeEach
    void beforeEach() {
        this.jpaDatasourceRepository.deleteAll();
        this.jpaInstrumentRepository.deleteAll();
        this.jpaIntradayValueRepository.deleteAll();
    }

    @Test
    @DisplayName("""
        T1. Получение списка всех инструментов
        """)
    void testCase1() {
        saveExchangeWithStocks();
        final Pagination<InstrumentEntity> pagination = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(Integer.MAX_VALUE)
                .build()
            );
        assertEquals(2, pagination.getTotalElements());
        assertTrue(pagination
            .getElements()
            .stream()
            .map(InstrumentEntity::getTicker)
            .toList()
            .containsAll(List.of("AFKS", "SBER"))
        );
    }

    @Test
    @DisplayName("""
        T2. Получение инструмента по его тикеру и источнику данных
        """)
    void testCase2() {
        saveExchangeWithStocks();
        assertEquals("AFKS", psqlDatasourceQueryService.findInstrumentBy(DATASOURCE_ID, "AFKS").getTicker());
        assertEquals("SBER", psqlDatasourceQueryService.findInstrumentBy(DATASOURCE_ID, "SBER").getTicker());
    }

    private void saveExchangeWithStocks() {
        final Datasource datasource = new Datasource(
            DatasourceId.from(DATASOURCE_ID),
            "test",
            "test",
            "test",
            List.of(
                createAfks(),
                createSber()
            )
        );
        datasourceRepository.save(datasource);
    }

    @Test
    @DisplayName("""
        T3. Фильтрация инструментов по их типу.
        """)
    void testCase3() {
        Datasource datasource = new Datasource(
            DatasourceId.from(UUID.randomUUID()),
            "test",
            "test",
            "test",
            List.of(createAfks(), createImoex())
        );
        datasourceRepository.save(datasource);

        final Pagination<InstrumentEntity> stocks = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .type(InstrumentType.STOCK)
                .build()
            );
        final Pagination<InstrumentEntity> indexes = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .type(InstrumentType.INDEX)
                .build()
            );
        assertEquals(1, stocks.getTotalElements());
        assertEquals(1, stocks.getElements().size());
        assertEquals(1, indexes.getTotalElements());
        assertEquals(1, indexes.getElements().size());
    }

    @Test
    @DisplayName("""
        T4. Фильтрация инструментов по их тикеру.
        """)
    void testCase4() {
        final Datasource datasource = new Datasource(
            DatasourceId.from(UUID.randomUUID()),
            "test",
            "test",
            "test",
            List.of(
                createAfks(),
                createSber(),
                createSberp(),
                createImoex()
            )
        );
        datasourceRepository.save(datasource);

        final Pagination<InstrumentEntity> afks = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .ticker("AFKS")
                .build()
            );
        final Pagination<InstrumentEntity> imoex = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .ticker("IMOEX")
                .build()
            );
        final Pagination<InstrumentEntity> sbers = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .ticker("SBER")
                .build()
            );

        assertEquals(1, afks.getTotalElements());
        assertEquals(1, afks.getElements().size());

        assertEquals(1, imoex.getTotalElements());
        assertEquals(1, imoex.getElements().size());

        assertEquals(2, sbers.getTotalElements());
        assertEquals(1, sbers.getElements().size());
    }

    @Test
    @DisplayName("""
        T5. Фильтрация инструментов по их краткому наименованию.
        """)
    void testCase5() {
        final Datasource datasource = new Datasource(
            DatasourceId.from(UUID.randomUUID()),
            "test",
            "test",
            "test",
            List.of(
                createSber(),
                createSberp()
            )
        );
        datasourceRepository.save(datasource);

        final Pagination<InstrumentEntity> instruments = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(2)
                .shortName("Сбер")
                .build()
            );

        assertEquals(2, instruments.getTotalElements());
        assertEquals(2, instruments.getElements().size());
    }

    @Test
    @DisplayName("""
        T6. Фильтрация инструментов по их типу и тикеру.
        """)
    void testCase6() {
        final Datasource datasource = new Datasource(
            DatasourceId.from(UUID.randomUUID()),
            "test",
            "test",
            "test",
            List.of(
                createAfks(),
                createSber(),
                createImoex()
            )
        );
        datasourceRepository.save(datasource);

        final Pagination<InstrumentEntity> sber = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .ticker("SBER")
                .type(InstrumentType.STOCK)
                .build()
            );
        final Pagination<InstrumentEntity> imoex = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .ticker("IMOEX")
                .type(InstrumentType.INDEX)
                .build()
            );

        assertEquals(1, sber.getTotalElements());
        assertEquals(1, sber.getElements().size());

        assertEquals(1, imoex.getTotalElements());
        assertEquals(1, imoex.getElements().size());
    }

    @Test
    @DisplayName("""
        T7. Фильтрация инструментов по их тикеру, типу и краткому наименованию.
        """)
    void testCase7() {
        final Datasource datasource = new Datasource(
            DatasourceId.from(UUID.randomUUID()),
            "test",
            "test",
            "test",
            List.of(
                createAfks(),
                createSberp(),
                createImoex()
            )
        );
        datasourceRepository.save(datasource);

        final Pagination<InstrumentEntity> sberp = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .ticker("SBER")
                .shortName("Сбербанк-п")
                .type(InstrumentType.STOCK)
                .build()
            );

        final Pagination<InstrumentEntity> imoex = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .ticker("IMOEX")
                .shortName("Индекс")
                .type(InstrumentType.INDEX)
                .build()
            );

        assertEquals(1, sberp.getTotalElements());
        assertEquals(1, sberp.getElements().size());

        assertEquals(1, imoex.getTotalElements());
        assertEquals(1, imoex.getElements().size());
    }

    @Test
    @DisplayName("""
        T8. Постраничное получение данных
        """)
    void testCase8() {
        final Datasource datasource = new Datasource(
            DatasourceId.from(UUID.randomUUID()),
            "test",
            "test",
            "test",
            List.of(
                createAfks(),
                createSber(),
                createSberp(),
                createImoex()
            )
        );
        datasourceRepository.save(datasource);
        final Pagination<InstrumentEntity> instruments1 = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(1)
                .orderField("ticker")
                .orderDirection("ASC")
                .build()
            );
        final Pagination<InstrumentEntity> instruments2 = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(1)
                .pageSize(1)
                .orderField("ticker")
                .orderDirection("ASC")
                .build()
            );
        final Pagination<InstrumentEntity> instruments3 = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(1)
                .pageSize(2)
                .orderField("ticker")
                .orderDirection("ASC")
                .build()
            );
        final Pagination<InstrumentEntity> instruments4 = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(3)
                .orderField("ticker")
                .orderDirection("ASC")
                .build()
            );

        assertEquals(4, instruments1.getTotalElements());
        assertEquals(4, instruments1.getTotalPages());
        assertEquals(1, instruments1.getElements().size());

        assertEquals(4, instruments2.getTotalElements());
        assertEquals(4, instruments2.getTotalPages());
        assertEquals(1, instruments2.getElements().size());

        assertEquals(4, instruments3.getTotalElements());
        assertEquals(2, instruments3.getTotalPages());
        assertEquals(2, instruments3.getElements().size());

        assertEquals(4, instruments4.getTotalElements());
        assertEquals(2, instruments4.getTotalPages());
        assertEquals(3, instruments4.getElements().size());
    }

    @Test
    @DisplayName("""
        T9. Сортировка данных
        """)
    void testCase9() {
        final Datasource datasource = new Datasource(
            DatasourceId.from(UUID.randomUUID()),
            "test",
            "test",
            "test",
            List.of(
                createAfks(),
                createSber(),
                createSberp(),
                createImoex()
            )
        );
        datasourceRepository.save(datasource);

        final Pagination<InstrumentEntity> instruments1 = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(4)
                .orderDirection("DESC")
                .orderField("details.shortName")
                .build()
            );
        final Pagination<InstrumentEntity> instruments2 = psqlDatasourceQueryService
            .getPagination(InstrumentFilterParams
                .builder()
                .pageNumber(0)
                .pageSize(4)
                .orderDirection("ASC")
                .orderField("details.shortName")
                .build()
            );

        assertEquals("AFKS", instruments1.getElements().get(0).getTicker());
        assertEquals("SBERP", instruments1.getElements().get(1).getTicker());
        assertEquals("SBER", instruments1.getElements().get(2).getTicker());
        assertEquals("IMOEX", instruments1.getElements().get(3).getTicker());

        assertEquals("IMOEX", instruments2.getElements().get(0).getTicker());
        assertEquals("SBER", instruments2.getElements().get(1).getTicker());
        assertEquals("SBERP", instruments2.getElements().get(2).getTicker());
        assertEquals("AFKS", instruments2.getElements().get(3).getTicker());
    }
}
