package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.configurator.command.SaveAnomalyVolumeScanner;
import ru.ioque.investfund.domain.configurator.command.SaveCorrelationSectoralScanner;
import ru.ioque.investfund.domain.configurator.command.SavePrefSimpleScanner;
import ru.ioque.investfund.domain.configurator.command.SaveScannerCommand;
import ru.ioque.investfund.domain.configurator.command.SaveSectoralRetardScanner;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.command.AddDatasourceCommand;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseScannerConfiguratorTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        datasourceManager().registerDatasource(
            AddDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        prepareDatasource();
        loggerProvider().clearLogs();
    }

    protected UUID getDatasourceId() {
        return datasourceRepository()
            .getAll()
            .get(0)
            .getId();
    }

    protected UUID getScannerId() {
        return getScanner().getId();
    }

    protected SignalScanner getScanner() {
        return scannerRepository()
            .getAll()
            .get(0);
    }

    private void prepareDatasource() {
        exchangeDataFixture().initInstruments(
            List.of(imoex(),
                tgkb(),
                tgkn(),
                sber(),
                sberP(),
                rosn(),
                sibn(),
                lkoh(),
                tatn(),
                brf4(),
                usdRub())
        );
        datasourceManager().integrateInstruments(getDatasourceId());
        datasourceManager().enableUpdate(getDatasourceId(), getTickers(getDatasourceId()));
    }

    protected void testAddNewScannerError(SaveScannerCommand command, String msg) {
        var error = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );
        assertEquals(msg, error.getMessage());
    }

    protected void testUpdateScannerError(UUID id, SaveScannerCommand command, String msg) {
        var error = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().updateScanner(id, command)
        );
        assertEquals(msg, error.getMessage());
    }

    protected SaveAnomalyVolumeScanner.SaveAnomalyVolumeScannerBuilder buildSaveAnomalyVolumeScannerWith() {
        return SaveAnomalyVolumeScanner.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(List.of("TGKN", "TGKB", "IMOEX"))
            .indexTicker("IMOEX")
            .historyPeriod(180)
            .scaleCoefficient(1.5);
    }

    protected SaveSectoralRetardScanner.SaveSectoralRetardScannerBuilder buildSaveSectoralRetardScannerWith() {
        return SaveSectoralRetardScanner.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(List.of("TGKN", "TGKB", "IMOEX"))
            .historyScale(0.015)
            .intradayScale(0.015);
    }

    protected SaveCorrelationSectoralScanner.SaveCorrelationSectoralScannerBuilder buildSaveCorrelationSectoralScannerWith() {
        return SaveCorrelationSectoralScanner.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(List.of("TGKN", "TGKB", "IMOEX"))
            .futuresOvernightScale(0.015)
            .stockOvernightScale(0.015)
            .futuresTicker("BRF4");
    }

    protected SavePrefSimpleScanner.SavePrefSimpleScannerBuilder buildSavePrefSimpleScannerWith() {
        return SavePrefSimpleScanner.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(List.of("TGKN", "TGKB", "IMOEX"))
            .spreadParam(1.0);
    }

    protected String descIsEmpty() {
        return "Не передано описание.";
    }

    protected String datasourceIdIsEmpty() {
        return "Не передан идентификатор источника данных.";
    }

    protected String workPeriodIsEmpty() {
        return "Не передан период работы сканера.";
    }

    protected String tickersIsEmpty() {
        return "Не передан список тикеров анализируемых инструментов.";
    }

    protected String workPeriodIsNegative() {
        return "Период работы сканера должен быть положительным целым числом.";
    }
}
