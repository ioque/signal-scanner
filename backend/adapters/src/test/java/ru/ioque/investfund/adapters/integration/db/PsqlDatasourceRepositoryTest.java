package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PSQL DATASOURCE REPOSITORY TEST")
public class PsqlDatasourceRepositoryTest extends DatabaseTest {
    DatasourceRepository datasourceRepository;

    public PsqlDatasourceRepositoryTest(
        @Autowired DatasourceRepository datasourceRepository
    ) {
        this.datasourceRepository = datasourceRepository;
    }

    @Test
    @DisplayName("Т1. Сохранение данных о бирже без инструментов")
    void testCase1() {
        final Datasource datasource = new Datasource(
            MOEX_DATASOURCE_ID,
            "test",
            "test",
            "test",
            List.of()
        );

        datasourceRepository.save(datasource);

        assertTrue(datasourceRepository.getBy(MOEX_DATASOURCE_ID).isPresent());
        assertEquals(datasource, datasourceRepository.getBy(MOEX_DATASOURCE_ID).get());
    }

    @Test
    @DisplayName("Т2. Сохранение данных о бирже с инструментами")
    void testCase2() {
        final Instrument instrument = createTgkn();
        final Datasource datasource = new Datasource(
            MOEX_DATASOURCE_ID,
            "test",
            "test",
            "test",
            List.of(
                instrument
            )
        );

        datasourceRepository.save(datasource);

        assertTrue(datasourceRepository.getBy(MOEX_DATASOURCE_ID).isPresent());
        assertEquals(1, datasourceRepository.getBy(MOEX_DATASOURCE_ID).get().getInstruments().size());
        assertEquals(instrument, datasourceRepository.getBy(MOEX_DATASOURCE_ID).get().getInstruments().get(0));
    }
}
