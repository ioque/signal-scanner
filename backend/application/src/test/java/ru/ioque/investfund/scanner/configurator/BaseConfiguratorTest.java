package ru.ioque.investfund.scanner.configurator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.AddDatasourceCommand;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.AnomalyVolumeProperties;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseConfiguratorTest extends BaseTest {
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

    protected UUID getFirstScannerId() {
        return scannerRepository().getScannerMap().keySet().iterator().next();
    }

    protected SignalScanner getScanner(UUID scannerId) {
        return scannerRepository().getBy(scannerId).orElseThrow();
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

    protected void testAddNewScannerError(CreateScannerCommand command, String msg) {
        var error = assertThrows(
            ConstraintViolationException.class,
            () -> scannerManager().createScanner(command)
        );
        assertEquals(msg, getMessage(error));
    }

    protected void testUpdateScannerError(UpdateScannerCommand command, String msg) {
        var error = assertThrows(
            ConstraintViolationException.class,
            () -> scannerManager().updateScanner(command)
        );
        assertEquals(msg, getMessage(error));
    }

    protected CreateScannerCommand.CreateScannerCommandBuilder buildCreateAnomalyVolumeScannerWith() {
        return CreateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(List.of("TGKN", "TGKB", "IMOEX"))
            .properties(
                AnomalyVolumeProperties.builder()
                    .indexTicker("IMOEX")
                    .historyPeriod(180)
                    .scaleCoefficient(1.5)
                    .build()
            );
    }

    protected UpdateScannerCommand.UpdateScannerCommandBuilder buildUpdateAnomalyVolumeScannerWith() {
        return UpdateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .tickers(List.of("TGKN", "TGKB", "IMOEX"))
            .properties(
                AnomalyVolumeProperties.builder()
                    .indexTicker("IMOEX")
                    .historyPeriod(180)
                    .scaleCoefficient(1.5)
                    .build()
            );
    }

    protected String getMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElseThrow();
    }

    protected String descIsEmpty() {
        return "Не передано описание сканера.";
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
        return "Период работы сканера должен быть больше 0.";
    }
}
