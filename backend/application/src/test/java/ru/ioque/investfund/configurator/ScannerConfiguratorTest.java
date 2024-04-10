package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.AddDatasourceCommand;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.domain.configurator.command.AddNewScannerCommand;
import ru.ioque.investfund.domain.configurator.entity.AnomalyVolumeAlgorithmConfig;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.core.ValidatorException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScannerConfiguratorTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        exchangeManager().registerDatasource(
            AddDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        prepareDatasource();
        loggerProvider().clearLogs();
    }

    @Test
    @DisplayName("""
        T1. В конфигурации сканера передан несуществующий источник данных.
        Выброшена ошибка, текст ошибки: "Источник данных с идентификатором {} не найден.".
        """)
    void testCase1() {
        final AddNewScannerCommand command = AddNewScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(UUID.randomUUID())
            .tickers(List.of("TGKN", "IMOEX"))
            .algorithmConfig(defaultAnomalyVolumeAlgorithmConfig())
            .build();

        final ApplicationException exception = assertThrows(
            ApplicationException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );
        assertEquals(
            "Источник данных не найден.",
            exception.getMessage()
        );
    }

    @Test
    @DisplayName("""
        T2. В конфигурации сканера передан тикер несуществующего инструмента.
        Выброшена ошибка, текст ошибки: "Инструмент с тикерм {} не найден.".
        """)
    void testCase2() {
        final AddNewScannerCommand command = AddNewScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(List.of("TGKN", "LVHK", "TGKM", "IMOEX"))
            .algorithmConfig(defaultAnomalyVolumeAlgorithmConfig())
            .build();

        final ValidatorException exception = assertThrows(
            ValidatorException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(2, exception.getErrors().size());
        assertTrue(
            exception.getErrors().containsAll(List.of(
                "Инструмент с тикером LVHK не найден.",
                "Инструмент с тикером TGKM не найден."
            ))
        );
    }

    @Test
    @DisplayName("""
        T3. В конфигурации сканера передан тикер несуществующего инструмента.
        Выброшена ошибка, текст ошибки: "Инструмент с тикерм {} не найден.".
        """)
    void testCase3() {
        final AddNewScannerCommand command = AddNewScannerCommand.builder()
            .workPeriodInMinutes(1)
            .datasourceId(getDatasourceId())
            .tickers(List.of("TGKN", "IMOEX"))
            .algorithmConfig(defaultAnomalyVolumeAlgorithmConfig())
            .build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(
            "Не передано описание.",
            exception.getMessage()
        );
    }

    @Test
    @DisplayName("""
        T4. В конфигурации сканера передан тикер несуществующего инструмента.
        Выброшена ошибка, текст ошибки: "Инструмент с тикерм {} не найден.".
        """)
    void testCase4() {
        final AddNewScannerCommand command = AddNewScannerCommand.builder()
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(List.of("TGKN", "IMOEX"))
            .algorithmConfig(defaultAnomalyVolumeAlgorithmConfig())
            .build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(
            "Не передан период работы сканера.",
            exception.getMessage()
        );
    }

    private void prepareDatasource() {
        exchangeDataFixture().initInstruments(
            List.of(imoex(), tgkb(), tgkn(), sber(), sberP(), brf4(), usdRub())
        );
        exchangeManager().integrateInstruments(getDatasourceId());
        exchangeManager().enableUpdate(getDatasourceId(), getTickers(getDatasourceId()));
    }

    private AnomalyVolumeAlgorithmConfig defaultAnomalyVolumeAlgorithmConfig() {
        return new AnomalyVolumeAlgorithmConfig(
            1.5,
            180,
            "IMOEX"
        );
    }

    private UUID getDatasourceId() {
        return exchangeRepository().getAll().get(0).getId();
    }
}
