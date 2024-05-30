package ru.ioque.investfund.datasource;

import java.util.List;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.TGKN;


public class EnableUpdateInstrumentTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. В команде на активацию обновления инструментов не передан идентификатор источника данных.
        """)
    void testCase1() {
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
        T2. В команде на активацию обновления инструментов не передан список тикеров.
        """)
    void testCase2() {
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
        T3. В команде на активацию обновления инструментов не передан список тикеров.
        """)
    void testCase3() {
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
        T4. В команде на активацию обновления инструментов в список тикеров передана пустая строка.
        """)
    void testCase4() {
        registerDatasource();
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new EnableUpdateInstruments(getDatasourceId(), List.of(new Ticker(TGKN), new Ticker(""))))
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Тикер должен быть непустой строкой, состоящей из латинских букв или цифр.", getMessage(exception));
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
