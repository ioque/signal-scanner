package ru.ioque.investfund.datasource.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.datasource.integration.IntegrationValidationException;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.StockDto;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("INSTRUMENT INTEGRATION TEST")
public class InstrumentIntegrationTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
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
            imoex(),
            afks()
        );

        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));

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
        initInstrumentDetails(afks(), imoex(), brf4());
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        clearLogs();

        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        assertEquals(3, getInstruments(getDatasourceId()).size());
        assertEquals(getDatasourceId(), datasourceRepository().findBy(getDatasourceId()).orElseThrow().getId());
    }

    @Test
    @DisplayName("""
        T3. Повторная интеграция инструментов, в источнике данных появились новые инструменты.
        """)
    void testCase3() {
        initInstrumentDetails(afks(), imoex(), brf4());
        initTodayDateTime("2023-12-12T10:00:00");
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        clearLogs();
        initInstrumentDetails(afks(), imoex(), brf4(), lkohDetails(), rosnDetails(), sibn());

        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));

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
            StockDto.builder()
                .ticker(AFKS)
                .shortName("ао Супер Система")
                .name("АФК Супер Система")
                .lotSize(100000)
                .isin("RU000A0DQZE3")
                .listLevel(2)
                .regNumber("1-05-01669-A")
                .build()
        ));
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        assertEquals(1, getInstruments(getDatasourceId()).size());
    }

    @Test
    @DisplayName("""
        T5. Интеграция инструментов, есть невалидные данные.
        """)
    void testCase5() {
        initTodayDateTime("2023-12-12T10:00:00");
        datasourceStorage().initInstrumentDetails(List.of(
            StockDto.builder()
                .ticker("ТРЕНЬК_ПЕНЬК")
                .shortName("ао Супер Система")
                .name("АФК Супер Система")
                .lotSize(100000)
                .isin("RU000A0DQZE3")
                .listLevel(-1)
                .regNumber("1-05-01669-A")
                .build(),
            StockDto.builder()
                .ticker(AFKS)
                .shortName("ао Супер Система")
                .name("АФК Супер Система")
                .lotSize(0)
                .isin("1111")
                .listLevel(0)
                .regNumber("1-05-01669-A")
                .build(),
            StockDto.builder().build()
        ));
        final IntegrationValidationException error = assertThrows(
            IntegrationValidationException.class,
            () -> commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()))
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
