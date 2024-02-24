package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.storage.jpa.filter.InstrumentFilterParams;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("INSTRUMENT QUERY REPOSITORY TEST")
public class JpaInstrumentQueryRepositoryTest extends BaseJpaTest {
    JpaInstrumentQueryRepository instrumentQueryRepository;
    InstrumentEntityRepository instrumentEntityRepository;

    public JpaInstrumentQueryRepositoryTest(
        @Autowired JpaInstrumentQueryRepository instrumentQueryRepository,
        @Autowired InstrumentEntityRepository instrumentEntityRepository
    ) {
        this.instrumentQueryRepository = instrumentQueryRepository;
        this.instrumentEntityRepository = instrumentEntityRepository;
    }

    @Test
    @DisplayName("""
        T1. Получение списка всех инструментов
        """)
    void testCase1() {
        saveExchangeWithStocks(UUID.randomUUID(), UUID.randomUUID());
        var list = instrumentQueryRepository.getAll();
        assertEquals(2, list.size());
        assertTrue(list.stream().map(Instrument::getTicker).toList().containsAll(List.of("AFKS", "SBER")));
    }

    @Test
    @DisplayName("""
        T2. Получение инструмента по его идентификатору
        """)
    void testCase2() {
        final UUID id1 = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();
        saveExchangeWithStocks(id1, id2);
        assertEquals("AFKS", instrumentQueryRepository.getById(id1).getTicker());
        assertEquals("SBER", instrumentQueryRepository.getById(id2).getTicker());
    }

    private void saveExchangeWithStocks(UUID id1, UUID id2) {
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(id1, id2),
            List.of(
                buildStockWith().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildStockWith().id(id2).ticker("SBER").name("SBER").shortName("SBER").build()
            )
        );
        exchangeRepository.save(exchange);
    }

    @Test
    @DisplayName(
        """
        T3. Фильтрация инструментов по их типу.
        """)
    void testCase3() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(id1, id2),
            List.of(
                buildStockWith().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildIndexWith().id(id2).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        exchangeRepository.save(exchange);

        List<Instrument> stocks = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().type("stock").build());
        List<Instrument> indexes = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().type("index").build());

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
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(id1, id2),
            List.of(
                buildStockWith().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildStockWith().id(id2).ticker("SBER").name("SBER").shortName("SBER").build(),
                buildStockWith().id(id3).ticker("SBERP").name("SBERP").shortName("SBERP").build(),
                buildIndexWith().id(id4).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        exchangeRepository.save(exchange);

        List<Instrument> afks = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().ticker("AFKS").build());
        List<Instrument> imoex = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().ticker("IMOEX").build());
        List<Instrument> sbers = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().ticker("SBER").build());

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
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(id1, id2),
            List.of(
                buildStockWith().id(id1).ticker("SBER").name("ПАО Сбербанк").shortName("Сбербанк").build(),
                buildIndexWith().id(id2).ticker("SBERP").name("ПАО Сбербанк-п").shortName("Сбербанк-п").build()
            )
        );
        exchangeRepository.save(exchange);

        List<Instrument> instruments = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().shortName("Сбер").build());

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
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(id1, id2, id3),
            List.of(
                buildStockWith().id(id1).ticker("AFKS").name("AFKS").shortName("AFKS").build(),
                buildStockWith().id(id2).ticker("SBER").name("SBER").shortName("SBER").build(),
                buildIndexWith().id(id3).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        exchangeRepository.save(exchange);

        List<Instrument> sber = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().ticker("SBER").type("stock").build());
        List<Instrument> imoex = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().ticker("IMOEX").type("index").build());

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
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            Set.of(id1, id2, id3),
            List.of(
                buildStockWith().id(id1).ticker("SBER").name("AFKS").shortName("AFKS").build(),
                buildStockWith().id(id2).ticker("SBERP").name("SBER").shortName("Сбербанк-п").build(),
                buildIndexWith().id(id3).ticker("IMOEX").name("Индекс мосбиржи").shortName("Индекс мосбиржи").build()
            )
        );
        exchangeRepository.save(exchange);

        List<Instrument> sberp = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().ticker("SBER").shortName("Сбербанк-п").type("stock").build());
        List<Instrument> imoex = instrumentQueryRepository.getAll(InstrumentFilterParams.builder().ticker("IMOEX").type("index").build());

        assertEquals(1, sberp.size());
        assertEquals(1, imoex.size());
    }

    @Test
    @DisplayName("""
        T8. Постраничное получение данных
        """)
    void testCase8() {

    }

    @Test
    @DisplayName("""
        T9. Сортировка данных
        """)
    void testCase9() {

    }
}
