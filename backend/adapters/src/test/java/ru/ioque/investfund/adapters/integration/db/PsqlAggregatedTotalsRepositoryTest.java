package ru.ioque.investfund.adapters.integration.db;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.psql.PsqlAggregatedTotalsRepository;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PsqlAggregatedTotalsRepositoryTest extends DatabaseTest {
    @Autowired
    private PsqlAggregatedTotalsRepository repository;

    @BeforeEach
    public void setUp() {
        initializeDatasource();

    }

    @Test
    @DisplayName("""
        T1. Сохранение списка агрегированных итогов
        """)
    void testCase1() {
        repository.saveAll(getAggregatedTotals());

        assertEquals(getAggregatedTotals(), repository.findAllBy(AFKS_ID));
    }

    @Test
    @DisplayName("""
        T2. Получение актуальной записи
        """)
    void testCase2() {
        repository.saveAll(getAggregatedTotals());

        final Optional<AggregatedTotals> aggregatedTotals = repository.findActualBy(AFKS_ID);
        assertEquals(
            getAggregatedTotals().stream().max(AggregatedTotals::compareTo).orElseThrow(),
            aggregatedTotals.orElseThrow()
        );
    }

    private List<AggregatedTotals> getAggregatedTotals() {
        return List.of(
            AggregatedTotals.builder()
                .ticker(AFKS)
                .instrumentId(AFKS_ID)
                .date(LocalDate.parse("2024-05-25"))
                .closePrice(11D)
                .openPrice(10D)
                .highPrice(12D)
                .lowPrice(9D)
                .waPrice(10D)
                .value(1_000_000D)
                .build(),
            AggregatedTotals.builder()
                .ticker(AFKS)
                .instrumentId(AFKS_ID)
                .date(LocalDate.parse("2024-05-24"))
                .closePrice(21D)
                .openPrice(20D)
                .highPrice(22D)
                .lowPrice(19D)
                .waPrice(20D)
                .value(11_500_000D)
                .build(),
            AggregatedTotals.builder()
                .ticker(AFKS)
                .instrumentId(AFKS_ID)
                .date(LocalDate.parse("2024-05-23"))
                .closePrice(31D)
                .openPrice(30D)
                .highPrice(32D)
                .lowPrice(29D)
                .waPrice(20D)
                .value(22_000_000D)
                .build(),
            AggregatedTotals.builder()
                .ticker(AFKS)
                .instrumentId(AFKS_ID)
                .date(LocalDate.parse("2024-05-22"))
                .closePrice(41D)
                .openPrice(40D)
                .highPrice(42D)
                .lowPrice(39D)
                .waPrice(40D)
                .value(44_000_000D)
                .build(),
            AggregatedTotals.builder()
                .ticker(AFKS)
                .instrumentId(AFKS_ID)
                .date(LocalDate.parse("2024-05-21"))
                .closePrice(51D)
                .openPrice(50D)
                .highPrice(52D)
                .lowPrice(49D)
                .waPrice(50D)
                .value(555_000_000D)
                .build()
        );
    }
}
