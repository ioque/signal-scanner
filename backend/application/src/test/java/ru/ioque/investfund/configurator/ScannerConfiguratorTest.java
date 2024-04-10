package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.configurator.AddNewScannerCommand;
import ru.ioque.investfund.application.modules.datasource.AddDatasourceCommand;
import ru.ioque.investfund.application.share.exception.ValidatorException;
import ru.ioque.investfund.domain.configurator.AnomalyVolumeAlgorithmConfig;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        final ValidatorException exception = assertThrows(
            ValidatorException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(1, exception.getErrors().size());
        assertEquals(
            "Источник данных с идентификатором " + command.getDatasourceId() + " не найден.",
            exception.getErrors().get(0)
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
