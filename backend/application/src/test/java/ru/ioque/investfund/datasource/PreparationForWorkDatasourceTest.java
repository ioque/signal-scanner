package ru.ioque.investfund.datasource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.domain.datasource.validator.ValidationException;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.value.details.StockDetail;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PREPARATION FOR WORK DATASOURCE TEST")
public class PreparationForWorkDatasourceTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
    }

    @Test
    @DisplayName("""
        T1. Интеграция инструментов, без дублей и невалидных данных.
        """)
    void testCase1() {
        initTodayDateTime("2023-12-12T10:00:00");
        initInstrumentDetails(
            imoexDetails(),
            afks()
        );

        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));

        final Optional<Datasource> datasource = datasourceRepository().findBy(getDatasourceId());
        assertTrue(datasource.isPresent());
        assertEquals("Московская биржа", datasource.get().getName());
        assertEquals("http://localhost:8080", datasource.get().getUrl());
        assertEquals("Московская биржа", datasource.get().getDescription());
        assertEquals(2, datasource.get().getInstruments().size());
        assertEquals(2, getInstruments(getDatasourceId()).size());
    }

    @Test
    @DisplayName("""
        T2. Повторная интеграция инструментов без изменения старых данных и добавления новых.
        """)
    void testCase2() {
        initTodayDateTime("2023-12-12T10:00:00");
        initInstrumentDetails(afks(), imoexDetails(), brf4());
        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));
        clearLogs();

        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));
        assertEquals(3, getInstruments(getDatasourceId()).size());
        assertEquals(getDatasourceId(), datasourceRepository().findBy(getDatasourceId()).orElseThrow().getId());
    }

    @Test
    @DisplayName("""
        T3. Повторная интеграция инструментов, в источнике данных появились новые инструменты.
        """)
    void testCase3() {
        initInstrumentDetails(afks(), imoexDetails(), brf4());
        initTodayDateTime("2023-12-12T10:00:00");
        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));
        clearLogs();
        initInstrumentDetails(afks(), imoexDetails(), brf4(), lkohDetails(), rosnDetails(), sibn());

        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));

        assertEquals(6, getInstruments(getDatasourceId()).size());
    }

    @Test
    @DisplayName("""
        T4. Интеграция инструментов, в данных есть дубликат по тикеру.
        """)
    void testCase4() {
        initTodayDateTime("2023-12-12T10:00:00");
        datasourceStorage().initInstrumentDetails(List.of(
            afks(),
            StockDetail.builder()
                .ticker(Ticker.from(AFKS))
                .shortName("ао Супер Система")
                .name("АФК Супер Система")
                .lotSize(100000)
                .isin(Isin.from("RU000A0DQZE3"))
                .listLevel(2)
                .regNumber("1-05-01669-A")
                .build()
        ));
        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));
        assertEquals(1, getInstruments(getDatasourceId()).size());
    }

    @Test
    @DisplayName("""
        T5. Интеграция инструментов, есть невалидные данные.
        """)
    void testCase5() {
        initTodayDateTime("2023-12-12T10:00:00");
        datasourceStorage().initInstrumentDetails(List.of(
            StockDetail.builder()
                .ticker(Ticker.from("ТРЕНЬК_ПЕНЬК"))
                .shortName("ао Супер Система")
                .name("АФК Супер Система")
                .lotSize(100000)
                .isin(Isin.from("RU000A0DQZE3"))
                .listLevel(-1)
                .regNumber("1-05-01669-A")
                .build(),
            StockDetail.builder()
                .ticker(Ticker.from(AFKS))
                .shortName("ао Супер Система")
                .name("АФК Супер Система")
                .lotSize(0)
                .isin(Isin.from("1111"))
                .listLevel(0)
                .regNumber("1-05-01669-A")
                .build(),
            StockDetail.builder().build()
        ));
        final ValidationException error = assertThrows(
            ValidationException.class,
            () -> commandBus().execute(new SynchronizeDatasource(getDatasourceId()))
        );
        assertEquals(3, error.getValidationErrors().size());
        assertEquals(2, error.getValidationErrors().get(0).getErrors().size());
        assertEquals(3, error.getValidationErrors().get(1).getErrors().size());
        assertEquals(3, error.getValidationErrors().get(2).getErrors().size());
        assertTrue(error.getValidationErrors().get(0).getErrors().contains("Тикер должен быть непустой строкой, состоящей из латинских букв или цифр."));
        assertTrue(error.getValidationErrors().get(0).getErrors().contains("Уровень листинга должен быть положительным целым числом."));
        assertTrue(error.getValidationErrors().get(1).getErrors().contains("Размер лота должен быть положительным целым числом."));
        assertTrue(error.getValidationErrors().get(1).getErrors().contains("Неккоретное значение ISIN."));
        assertTrue(error.getValidationErrors().get(1).getErrors().contains("Уровень листинга должен быть положительным целым числом."));
        assertTrue(error.getValidationErrors().get(2).getErrors().contains("Не заполнен тикер инструмента."));
        assertTrue(error.getValidationErrors().get(2).getErrors().contains("Не заполнено краткое наименование инструмента."));
        assertTrue(error.getValidationErrors().get(2).getErrors().contains("Не заполнено полное наименование инструмента."));
    }
}
