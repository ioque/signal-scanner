package ru.ioque.apitest.modules.datasource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.apitest.DatasourceEmulatedTest;
import ru.ioque.core.dto.datasource.request.DatasourceRequest;
import ru.ioque.core.dto.datasource.response.DatasourceResponse;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DATASOURCE CONFIGURATOR ACCEPTANCE TEST")
public class DatasourceConfiguratorAcceptanceTest extends DatasourceEmulatedTest {
    @Test
    @DisplayName("""
        T1. Регистрация нового источника данных.
        """)
    void testCase1() {
        final DatasourceRequest request = DatasourceRequest.builder()
            .name("Московская Биржа")
            .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
            .url(datasourceHost)
            .build();

        createDatasource(request);

        List<DatasourceResponse> datasourceList = getAllDatasource();
        assertEquals(1, datasourceList.size());
        DatasourceResponse datasource = getDatasourceBy(datasourceList.get(0).getId());
        assertEquals(request.getName(), datasource.getName());
        assertEquals(request.getDescription(), datasource.getDescription());
        assertEquals(request.getUrl(), datasource.getUrl());
    }

    @Test
    @DisplayName("""
        T2. Обновление конфигурации источника данных.
        """)
    void testCase2() {
        createDatasource(DatasourceRequest.builder()
            .name("Московская Биржа")
            .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
            .url(datasourceHost)
            .build());
        final DatasourceRequest updateRequest = DatasourceRequest.builder()
            .name("Странная Биржа")
            .description("Странная биржа кукусуку.")
            .url("http://localhost:8888")
            .build();
        List<DatasourceResponse> datasourceList = getAllDatasource();
        assertEquals(1, datasourceList.size());
        final UUID datasourceId = datasourceList.get(0).getId();

        updateDatasource(datasourceId, updateRequest);

        DatasourceResponse datasource = getDatasourceBy(datasourceList.get(0).getId());
        assertEquals(updateRequest.getName(), datasource.getName());
        assertEquals(updateRequest.getDescription(), datasource.getDescription());
        assertEquals(updateRequest.getUrl(), datasource.getUrl());
    }

    @Test
    @DisplayName("""
        T3. Удаление источника данных.
        """)
    void testCase3() {
        createDatasource(DatasourceRequest.builder()
            .name("Московская Биржа")
            .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
            .url(datasourceHost)
            .build());
        List<DatasourceResponse> datasourceList = getAllDatasource();
        assertEquals(1, datasourceList.size());
        final UUID datasourceId = datasourceList.get(0).getId();

        removeDatasource(datasourceId);

        var error = assertThrows(RuntimeException.class, () -> getDatasourceBy(datasourceId));
        assertEquals("Источник данных не зарегистрирован.", error.getMessage());
    }
}
