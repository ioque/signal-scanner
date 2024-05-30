package ru.ioque.investfund.datasource;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CREATE DATASOURCE TEST")
public class CreateDatasourceTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Успешная регистрация нового источника данных
        """)
    void testCase1() {
        final String datasourceName = "Московская биржа";
        final String datasourceUrl = "http://localhost:8080";

        commandBus().execute(
            CreateDatasource.builder()
                .name(datasourceName)
                .description(datasourceName)
                .url(datasourceUrl)
                .build()
        );

        Datasource datasource = datasourceRepository().findBy(getDatasourceId()).orElseThrow();
        assertEquals(datasourceName, datasource.getName());
        assertEquals(datasourceName, datasource.getDescription());
        assertEquals(datasourceUrl, datasource.getUrl());
        assertTrue(datasource.getInstruments().isEmpty());
        assertTrue(datasource.getUpdatableInstruments().isEmpty());
    }

    @Test
    @DisplayName("""
        T2. Регистрация нескольких источников данных
        """)
    void testCase2() {
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа 1")
                .description("Московская биржа 1")
                .url("http://localhost:8081")
                .build()
        );
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа 2")
                .description("Московская биржа 2")
                .url("http://localhost:8082")
                .build()
        );

        assertEquals(2, datasourceRepository().getAll().size());
    }

    @Test
    @DisplayName("""
        T3. В команде на регистрацию источника данных не передано название источника данных
        """)
    void testCase3() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                CreateDatasource.builder()
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
        T4. В команде на регистрацию источника данных не передано описание источника данных
        """)
    void testCase4() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                CreateDatasource.builder()
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
        T5. В команде на регистрацию источника данных не передан адрес источника данных
        """)
    void testCase5() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                CreateDatasource.builder()
                    .description("description")
                    .name("name")
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан адрес источника данных.", getMessage(exception));
    }
}
