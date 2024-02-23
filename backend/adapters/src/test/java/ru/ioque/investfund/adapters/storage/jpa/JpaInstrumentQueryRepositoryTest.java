package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.IndexEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.StockEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.application.adapters.InstrumentQueryRepository;
import ru.ioque.investfund.domain.exchange.entity.Exchange;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ioque.investfund.adapters.storage.jpa.specification.Specifications.typeSpecification;

@DisplayName("INSTRUMENT QUERY REPOSITORY TEST")
public class JpaInstrumentQueryRepositoryTest extends BaseJpaTest {
    InstrumentQueryRepository instrumentQueryRepository;
    InstrumentEntityRepository instrumentEntityRepository;

    public JpaInstrumentQueryRepositoryTest(
        @Autowired InstrumentQueryRepository instrumentQueryRepository,
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
        """
    )
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

        List<InstrumentEntity> stocks = instrumentEntityRepository.findAll(typeSpecification(StockEntity.class));
        List<InstrumentEntity> indexes = instrumentEntityRepository.findAll(typeSpecification(IndexEntity.class));

        assertEquals(1, stocks.size());
        assertEquals(1, indexes.size());
    }
}
