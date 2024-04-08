package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

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
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of()
        );
        datasourceRepository.saveDatasource(datasource);

        assertTrue(datasourceRepository.get().isPresent());
        assertEquals(datasource, datasourceRepository.get().get());
    }

    @Test
    @DisplayName("Т2. Сохранение данных о бирже с инструментами")
    void testCase2() {
        var instrument = buildAfks().build();
        Datasource datasource = new Datasource(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                instrument
            )
        );
        datasourceRepository.saveDatasource(datasource);

        assertTrue(datasourceRepository.get().isPresent());
        assertEquals(1, datasourceRepository.get().get().getInstruments().size());
        assertEquals(instrument, datasourceRepository.get().get().getInstruments().get(0));
    }
}
