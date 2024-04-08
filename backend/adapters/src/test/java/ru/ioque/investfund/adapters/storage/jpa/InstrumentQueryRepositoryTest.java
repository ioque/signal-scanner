package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.rest.DatasourceQueryService;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DATASOURCE QUERY SERVICE TEST")
public class InstrumentQueryRepositoryTest extends BaseJpaTest {
    DatasourceQueryService datasourceQueryService;
    JpaInstrumentRepository instrumentEntityRepository;

    public InstrumentQueryRepositoryTest(
        @Autowired DatasourceQueryService datasourceQueryService,
        @Autowired JpaInstrumentRepository instrumentEntityRepository
    ) {
        this.datasourceQueryService = datasourceQueryService;
        this.instrumentEntityRepository = instrumentEntityRepository;
    }

    @Test
    @DisplayName("""
        T1. Получение списка всех инструментов
        """)
    void testCase1() {
        saveExchangeWithStocks(UUID.randomUUID(), UUID.randomUUID());
        var list = datasourceQueryService.getAllInstruments();
        assertEquals(2, list.size());
        assertTrue(list.stream().map(InstrumentEntity::getTicker).toList().containsAll(List.of("AFKS", "SBER")));
    }

    @Test
    @DisplayName("""
        T2. Получение инструмента по его идентификатору
        """)
    void testCase2() {
        final UUID id1 = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();
        saveExchangeWithStocks(id1, id2);
        assertEquals("AFKS", datasourceQueryService.findInstrumentBy(id1).getTicker());
        assertEquals("SBER", datasourceQueryService.findInstrumentBy(id2).getTicker());
    }

    private void saveExchangeWithStocks(UUID id1, UUID id2) {
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildAfks().id(id2).ticker("SBER").name("SBER").shortName("SBER").build()
            )
        );
        datasourceRepository.saveDatasource(datasource);
    }

    @Test
    @DisplayName(
        """
            T3. Фильтрация инструментов по их типу.
            """)
    void testCase3() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildIndexWith().id(id2).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        datasourceRepository.saveDatasource(datasource);

        List<InstrumentEntity> stocks = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .type("stock")
            .build());
        List<InstrumentEntity> indexes = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .type("index")
            .build());

        assertEquals(1, stocks.size());
        assertEquals(1, indexes.size());
    }

    @Test
    @DisplayName(
        """
            T4. Фильтрация инструментов по их тикеру.
            """)
    void testCase4() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        UUID id4 = UUID.randomUUID();
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildAfks().id(id2).ticker("SBER").name("SBER").shortName("SBER").build(),
                buildAfks().id(id3).ticker("SBERP").name("SBERP").shortName("SBERP").build(),
                buildIndexWith().id(id4).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        datasourceRepository.saveDatasource(datasource);

        List<InstrumentEntity> afks = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker("AFKS")
            .build());
        List<InstrumentEntity> imoex = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker("IMOEX")
            .build());
        List<InstrumentEntity> sbers = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .ticker("SBER")
            .build());

        assertEquals(1, afks.size());
        assertEquals(1, imoex.size());
        assertEquals(2, sbers.size());
    }

    @Test
    @DisplayName(
        """
            T5. Фильтрация инструментов по их краткому наименованию.
            """)
    void testCase5() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks().id(id1).ticker("SBER").name("ПАО Сбербанк").shortName("Сбербанк").build(),
                buildIndexWith().id(id2).ticker("SBERP").name("ПАО Сбербанк-п").shortName("Сбербанк-п").build()
            )
        );
        datasourceRepository.saveDatasource(datasource);

        List<InstrumentEntity> instruments = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .shortName("Сбер")
            .build());

        assertEquals(2, instruments.size());
    }

    @Test
    @DisplayName(
        """
            T6. Фильтрация инструментов по их типу и тикеру.
            """)
    void testCase6() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildAfks().id(id2).ticker("SBER").name("SBER").shortName("SBER").build(),
                buildIndexWith().id(id3).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        datasourceRepository.saveDatasource(datasource);

        List<InstrumentEntity> sber = datasourceQueryService.findInstruments(InstrumentFilterParams.builder().ticker(
            "SBER").type("stock").build());
        List<InstrumentEntity> imoex = datasourceQueryService.findInstruments(InstrumentFilterParams.builder().ticker(
            "IMOEX").type("index").build());

        assertEquals(1, sber.size());
        assertEquals(1, imoex.size());
    }

    @Test
    @DisplayName(
        """
            T7. Фильтрация инструментов по их тикеру, типу и краткому наименованию.
            """
    )
    void testCase7() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks().id(id1).ticker("SBER").name("AFKS").shortName("AFKS").build(),
                buildAfks().id(id2).ticker("SBERP").name("SBER").shortName("Сбербанк-п").build(),
                buildIndexWith().id(id3).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        datasourceRepository.saveDatasource(datasource);

        List<InstrumentEntity> sberp = datasourceQueryService.findInstruments(InstrumentFilterParams.builder().ticker(
            "SBER").shortName("Сбербанк-п").type("stock").build());
        List<InstrumentEntity> imoex = datasourceQueryService.findInstruments(InstrumentFilterParams.builder().ticker(
            "IMOEX").type("index").build());

        assertEquals(1, sberp.size());
        assertEquals(1, imoex.size());
    }

    @Test
    @DisplayName("""
        T8. Постраничное получение данных
        """)
    void testCase8() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        UUID id4 = UUID.randomUUID();
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildAfks().id(id2).ticker("SBER").name("SBER").shortName("SBER").build(),
                buildAfks().id(id3).ticker("SBERP").name("SBERP").shortName("SBERP").build(),
                buildIndexWith().id(id4).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        datasourceRepository.saveDatasource(datasource);
        List<InstrumentEntity> instruments1 = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(0)
            .pageSize(1)
            .orderField("ticker")
            .orderDirection("ASC")
            .build());
        List<InstrumentEntity> instruments2 = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(1)
            .pageSize(1)
            .orderField("ticker")
            .orderDirection("ASC")
            .build());
        List<InstrumentEntity> instruments3 = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(1)
            .pageSize(2)
            .orderField("ticker")
            .orderDirection("ASC")
            .build());
        List<InstrumentEntity> instruments4 = datasourceQueryService.findInstruments(InstrumentFilterParams
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
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        UUID id4 = UUID.randomUUID();
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks().id(id1).ticker("AFKS").name("АФК Система").shortName("ао Система").build(),
                buildAfks().id(id2).ticker("SBER").name("ПАО Сбербанк").shortName("Сбербанк").build(),
                buildAfks().id(id3).ticker("SBERP").name("ПАО Сбербанк-п").shortName("Сбербанк-п").build(),
                buildIndexWith().id(id4).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        datasourceRepository.saveDatasource(datasource);

        List<InstrumentEntity> instruments = datasourceQueryService.findInstruments(InstrumentFilterParams
            .builder()
            .pageNumber(0)
            .pageSize(4)
            .orderDirection("DESC")
            .orderField("shortName")
            .build());
        List<InstrumentEntity> instruments2 = datasourceQueryService.findInstruments(InstrumentFilterParams
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
