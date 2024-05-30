package ru.ioque.investfund.datasource;

import java.util.UUID;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.UpdateDatasource;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UPDATE DATASOURCE TEST")
public class UpdateDatasourceTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Обновление данных источника данных.
        """)
    void testCase1() {
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        final String datasourceName = "Не Московская биржа";
        final String datasourceDesc = "Курлык курлык";
        final String datasourceUrl = "http://localhost:8082";

        commandBus().execute(
            UpdateDatasource.builder()
                .id(getDatasourceId())
                .name(datasourceName)
                .description(datasourceDesc)
                .url(datasourceUrl)
                .build()
        );

        Datasource datasource = datasourceRepository().findBy(getDatasourceId()).orElseThrow();
        assertEquals(datasourceName, datasource.getName());
        assertEquals(datasourceDesc, datasource.getDescription());
        assertEquals(datasourceUrl, datasource.getUrl());
        assertTrue(datasource.getInstruments().isEmpty());
        assertTrue(datasource.getUpdatableInstruments().isEmpty());
    }

    @Test
    @DisplayName("""
        T2. В команде на обновление источника данных не передан идентификатор источника данных
        """)
    void testCase2() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                UpdateDatasource.builder()
                    .description("description")
                    .name("name")
                    .url("http://datasource.ru")
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан идентификатор источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T3. В команде на обновление источника данных не передано название источника данных
        """)
    void testCase3() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                UpdateDatasource.builder()
                    .id(DatasourceId.from(UUID.randomUUID()))
                    .url("http://datasource.ru:8000")
                    .description("description")
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передано название источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T4. В команде на обновление источника данных не передано описание источника данных
        """)
    void testCase4() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                UpdateDatasource.builder()
                    .id(DatasourceId.from(UUID.randomUUID()))
                    .url("http://datasource.ru")
                    .name("name")
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передано описание источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T5. В команде на обновление источника данных не передан адрес источника данных
        """)
    void testCase5() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                UpdateDatasource.builder()
                    .id(DatasourceId.from(UUID.randomUUID()))
                    .description("description")
                    .name("name")
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан адрес источника данных.", getMessage(exception));
    }
}
