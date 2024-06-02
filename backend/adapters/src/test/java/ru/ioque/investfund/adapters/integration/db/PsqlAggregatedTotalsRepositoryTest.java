package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.psql.PsqlAggregatedTotalsRepository;

public class PsqlAggregatedTotalsRepositoryTest extends DatabaseTest {
    @Autowired
    private PsqlAggregatedTotalsRepository repository;

    @Test
    @DisplayName("""
        T1. Сохранение списка агрегированных итогов
        """)
    void testCase1() {

    }
}
