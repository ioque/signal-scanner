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
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PSQL DATASOURCE QUERY SERVICE TEST")
public class InstrumentEntityQueryRepositoryTest extends InfrastructureTest {
    private static final UUID DATASOURCE_ID = UUID.randomUUID();
    PsqlDatasourceQueryService psqlDatasourceQueryService;
    UUIDProvider uuidProvider;
    DatasourceRepository datasourceRepository;
    JpaDatasourceRepository jpaDatasourceRepository;
    JpaIntradayValueRepository jpaIntradayValueRepository;
    JpaInstrumentRepository jpaInstrumentRepository;

    public InstrumentEntityQueryRepositoryTest(
        @Autowired PsqlDatasourceQueryService psqlDatasourceQueryService,
        @Autowired UUIDProvider uuidProvider,
        @Autowired DatasourceRepository datasourceRepository,
        @Autowired JpaDatasourceRepository jpaDatasourceRepository,
        @Autowired JpaInstrumentRepository jpaInstrumentRepository,
        @Autowired JpaIntradayValueRepository jpaIntradayValueRepository
    ) {
        this.psqlDatasourceQueryService = psqlDatasourceQueryService;
        this.uuidProvider = uuidProvider;
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
        var list = psqlDatasourceQueryService.getAllInstruments();
        assertEquals(2, list.size());
        assertTrue(list.stream().map(InstrumentEntity::getTicker).toList().containsAll(List.of("AFKS", "SBER")));
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

        final List<InstrumentEntity> stocks = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .type("stock")
            .build());
        final List<InstrumentEntity> indexes = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .type("index")
            .build());

        assertEquals(1, stocks.size());
        assertEquals(1, indexes.size());
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

        final List<InstrumentEntity> afks = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker("AFKS")
            .build());
        final List<InstrumentEntity> imoex = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker("IMOEX")
            .build());
        final List<InstrumentEntity> sbers = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker("SBER")
            .build());

        assertEquals(1, afks.size());
        assertEquals(1, imoex.size());
        assertEquals(2, sbers.size());
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

        List<InstrumentEntity> instruments = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .shortName("Сбер")
            .build());

        assertEquals(2, instruments.size());
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

        List<InstrumentEntity> sber = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker(
                "SBER")
            .type("stock")
            .build());
        List<InstrumentEntity> imoex = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker(
                "IMOEX")
            .type("index")
            .build());

        assertEquals(1, sber.size());
        assertEquals(1, imoex.size());
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

        List<InstrumentEntity> sberp = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker(
                "SBER")
            .shortName("Сбербанк-п")
            .type("stock")
            .build());
        List<InstrumentEntity> imoex = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker(
                "IMOEX")
            .type("index")
            .build());

        assertEquals(1, sberp.size());
        assertEquals(1, imoex.size());
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
        final List<InstrumentEntity> instruments1 = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(0)
            .pageSize(1)
            .orderField("ticker")
            .orderDirection("ASC")
            .build());
        final List<InstrumentEntity> instruments2 = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(1)
            .pageSize(1)
            .orderField("ticker")
            .orderDirection("ASC")
            .build());
        final List<InstrumentEntity> instruments3 = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(1)
            .pageSize(2)
            .orderField("ticker")
            .orderDirection("ASC")
            .build());
        final List<InstrumentEntity> instruments4 = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(0)
            .pageSize(3)
            .orderField("ticker")
            .orderDirection("ASC")
            .build());

        assertEquals(1, instruments1.size());
        assertEquals(1, instruments2.size());
        assertEquals(2, instruments3.size());
        assertEquals(3, instruments4.size());
        assertTrue(instruments1.stream().map(InstrumentEntity::getTicker).toList().contains("AFKS"));
        assertTrue(instruments2.stream().map(InstrumentEntity::getTicker).toList().contains("IMOEX"));
        assertTrue(instruments3
            .stream()
            .map(InstrumentEntity::getTicker)
            .toList()
            .containsAll(List.of("SBER", "SBERP")));
        assertTrue(instruments4
            .stream()
            .map(InstrumentEntity::getTicker)
            .toList()
            .containsAll(List.of("AFKS", "IMOEX", "SBER")));
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

        final List<InstrumentEntity> instruments = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(0)
            .pageSize(4)
            .orderDirection("DESC")
            .orderField("shortName")
            .build());
        final List<InstrumentEntity> instruments2 = psqlDatasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(0)
            .pageSize(4)
            .orderDirection("ASC")
            .orderField("shortName")
            .build());

        assertEquals("AFKS", instruments.get(0).getTicker());
        assertEquals("SBERP", instruments.get(1).getTicker());
        assertEquals("SBER", instruments.get(2).getTicker());
        assertEquals("IMOEX", instruments.get(3).getTicker());

        assertEquals("IMOEX", instruments2.get(0).getTicker());
        assertEquals("SBER", instruments2.get(1).getTicker());
        assertEquals("SBERP", instruments2.get(2).getTicker());
        assertEquals("AFKS", instruments2.get(3).getTicker());
    }
}