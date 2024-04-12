package ru.ioque.investfund.datasource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.command.UpdateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatasourceConfiguratorTest extends BaseTest {
    @Test
    @DisplayName("""
        T1. Успешная регистрация нового источника данных
        """)
    void testCase1() {
        final String datasourceName = "Московская биржа";
        final String datasourceUrl = "http://localhost:8080";

        datasourceManager().registerDatasource(
            CreateDatasourceCommand.builder()
                .name(datasourceName)
                .description(datasourceName)
                .url(datasourceUrl)
                .build()
        );

        Datasource datasource = datasourceRepository().getBy(getDatasourceId()).orElseThrow();
        assertEquals(datasourceName, datasource.getName());
        assertEquals(datasourceName, datasource.getDescription());
        assertEquals(datasourceUrl, datasource.getUrl());
        assertTrue(datasource.getInstruments().isEmpty());
        assertTrue(datasource.getUpdatableInstruments().isEmpty());
    }

    @Test
    @DisplayName("""
        T1. Обновление данных источника данных.
        """)
    void testCase2() {
        datasourceManager().registerDatasource(
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        final String datasourceName = "Не Московская биржа";
        final String datasourceDesc = "Курлык курлык";
        final String datasourceUrl = "http://localhost:8082";

        datasourceManager().updateDatasource(
            UpdateDatasourceCommand.builder()
                .id(getDatasourceId())
                .name(datasourceName)
                .description(datasourceDesc)
                .url(datasourceUrl)
                .build()
        );

        Datasource datasource = datasourceRepository().getBy(getDatasourceId()).orElseThrow();
        assertEquals(datasourceName, datasource.getName());
        assertEquals(datasourceDesc, datasource.getDescription());
        assertEquals(datasourceUrl, datasource.getUrl());
        assertTrue(datasource.getInstruments().isEmpty());
        assertTrue(datasource.getUpdatableInstruments().isEmpty());
    }

    @Test
    @DisplayName("""
        T3. Регистрация нескольких источников данных
        """)
    void testCase3() {
        datasourceManager().registerDatasource(
            CreateDatasourceCommand.builder()
                .name("Московская биржа 1")
                .description("Московская биржа 1")
                .url("http://localhost:8081")
                .build()
        );
        datasourceManager().registerDatasource(
            CreateDatasourceCommand.builder()
                .name("Московская биржа 2")
                .description("Московская биржа 2")
                .url("http://localhost:8082")
                .build()
        );

        assertEquals(2, datasourceRepository().getAll().size());
    }
}
