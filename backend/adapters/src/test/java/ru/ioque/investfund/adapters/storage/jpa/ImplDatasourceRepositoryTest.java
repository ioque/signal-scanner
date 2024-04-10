package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EXCHANGE REPOSITORY TEST")
public class ImplDatasourceRepositoryTest extends BaseJpaTest {
    DatasourceRepository datasourceRepository;

    public ImplDatasourceRepositoryTest(
        @Autowired DatasourceRepository datasourceRepository
    ) {
        this.datasourceRepository = datasourceRepository;
    }

    @Test
    @DisplayName("Т1. Сохранение данных о бирже без инструментов")
    void testCase1() {
        final UUID datasourceId = UUID.randomUUID();
        final Datasource datasource = new Datasource(
            datasourceId,
            "test",
            "test",
            "test",
            List.of()
        );
        datasourceRepository.saveDatasource(datasource);

        assertTrue(datasourceRepository.getBy(datasourceId).isPresent());
        assertEquals(datasource, datasourceRepository.getBy(datasourceId).get());
    }

    @Test
    @DisplayName("Т2. Сохранение данных о бирже с инструментами")
    void testCase2() {
        final Instrument instrument = createTgkn();
        final UUID datasourceId = UUID.randomUUID();
        final Datasource datasource = new Datasource(
            datasourceId,
            "test",
            "test",
            "test",
            List.of(
                instrument
            )
        );
        datasourceRepository.saveDatasource(datasource);

        assertTrue(datasourceRepository.getBy(datasourceId).isPresent());
        assertEquals(1, datasourceRepository.getBy(datasourceId).get().getInstruments().size());
        assertEquals(instrument, datasourceRepository.getBy(datasourceId).get().getInstruments().get(0));
    }
}
