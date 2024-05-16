package ru.ioque.investfund.datasource;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.DisableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.datasource.command.ExecuteDatasourceWorker;
import ru.ioque.investfund.application.modules.datasource.command.UnregisterDatasource;
import ru.ioque.investfund.application.modules.datasource.command.UpdateDatasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DATASOURCE COMMAND VALIDATION TEST")
public class DatasourceCommandValidationTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. В команде на регистрацию источника данных не передано название источника данных
        """)
    void testCase1() {
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
        T2. В команде на регистрацию источника данных не передано описание источника данных
        """)
    void testCase2() {
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
        T3. В команде на регистрацию источника данных не передан адрес источника данных
        """)
    void testCase3() {
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

    @Test
    @DisplayName("""
        T4. В команде на удаление источника данных не передан идентификатор источника данных
        """)
    void testCase4() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new UnregisterDatasource(null))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан идентификатор источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T5. В команде на активацию обновления инструментов не передан идентификатор источника данных.
        """)
    void testCase5() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new EnableUpdateInstruments(null, List.of(new Ticker(TGKN))))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан идентификатор источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T6. В команде на активацию обновления инструментов не передан список тикеров.
        """)
    void testCase6() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new EnableUpdateInstruments(getDatasourceId(), List.of()))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан список тикеров инструментов для активации обновления торговых данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T7. В команде на активацию обновления инструментов не передан список тикеров.
        """)
    void testCase7() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new EnableUpdateInstruments(getDatasourceId(), null))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан список тикеров инструментов для активации обновления торговых данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T8. В команде на активацию обновления инструментов в список тикеров передана пустая строка.
        """)
    void testCase8() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new EnableUpdateInstruments(getDatasourceId(), List.of(new Ticker(TGKN), new Ticker(""))))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Тикер должен быть непустой строкой, состоящей из латинских букв или цифр.", getMessage(exception));
    }


    @Test
    @DisplayName("""
        T9. В команде на деактивацию обновления инструментов не передан идентификатор источника данных.
        """)
    void testCase9() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new DisableUpdateInstruments(null, List.of(new Ticker(TGKN))))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан идентификатор источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T10. В команде на деактивацию обновления инструментов не передан список тикеров.
        """)
    void testCase10() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new DisableUpdateInstruments(getDatasourceId(), List.of()))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан список тикеров инструментов для активации обновления торговых данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T11. В команде на деактивацию обновления инструментов не передан список тикеров.
        """)
    void testCase11() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new DisableUpdateInstruments(getDatasourceId(), null))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан список тикеров инструментов для активации обновления торговых данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T12. В команде на деактивацию обновления инструментов в список тикеров передана пустая строка.
        """)
    void testCase12() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new DisableUpdateInstruments(getDatasourceId(), List.of(new Ticker(TGKN), new Ticker(""))))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Тикер должен быть непустой строкой, состоящей из латинских букв или цифр.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T13. В команде на запуск интеграции инструментов не передан идентификатор источника данных.
        """)
    void testCase13() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new SynchronizeDatasource(null))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан идентификатор источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T14. В команде на запуск интеграции торговых данных не передан идентификатор источника данных.
        """)
    void testCase14() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new ExecuteDatasourceWorker(null))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не передан идентификатор источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T15. В команде на обновление источника данных не передано название источника данных
        """)
    void testCase15() {
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
        T16. В команде на обновление источника данных не передано описание источника данных
        """)
    void testCase16() {
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
        T17. В команде на обновление источника данных не передан адрес источника данных
        """)
    void testCase17() {
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

    @Test
    @DisplayName("""
        T18. В команде на обновление источника данных не передан идентификатор источника данных
        """)
    void testCase18() {
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

    private void registerDatasource() {
        commandBus().execute(
            CreateDatasource.builder()
                .description("description")
                .name("name")
                .url("http://datasource.ru")
                .build()
        );
    }
}
