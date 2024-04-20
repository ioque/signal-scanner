package ru.ioque.investfund.scanner.configurator;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseConfiguratorTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        prepareDatasource();
        loggerProvider().clearLogs();
    }

    protected ScannerId getFirstScannerId() {
        return scannerRepository().getScannerMap().keySet().iterator().next();
    }

    protected SignalScanner getScanner(ScannerId scannerId) {
        return scannerRepository().findBy(scannerId).orElseThrow();
    }

    private void prepareDatasource() {
        datasourceStorage().initInstrumentDetails(
            List.of(
                imoexDetails(),
                tgkbDetails(),
                tgknDetails(),
                sberDetails(),
                sberpDetails(),
                rosnDetails(),
                sibnDetails(),
                lkohDetails(),
                tatnDetails(),
                brf4Details(),
                usdRubDetails())
        );
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        commandBus().execute(new EnableUpdateInstrumentsCommand(getDatasourceId(), getTickers(getDatasourceId())));
    }

    protected void testAddNewScannerError(CreateScannerCommand command, String msg) {
        var error = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );
        assertEquals(msg, getMessage(error));
    }

    protected void testUpdateScannerError(UpdateScannerCommand command, String msg) {
        var error = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );
        assertEquals(msg, getMessage(error));
    }

    protected CreateScannerCommand.CreateScannerCommandBuilder buildCreateAnomalyVolumeScannerWith() {
        return CreateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(getAnomalyVolumeTickers())
            .properties(
                getDefaultAnomalyVolumeProperties()
            );
    }

    protected UpdateScannerCommand.UpdateScannerCommandBuilder buildUpdateAnomalyVolumeScannerWith() {
        return UpdateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .tickers(getAnomalyVolumeTickers())
            .properties(
                getDefaultAnomalyVolumeProperties()
            );
    }

    private List<@Valid Ticker> getAnomalyVolumeTickers() {
        return List.of(TGKN, TGKB, IMOEX);
    }

    private AnomalyVolumeProperties getDefaultAnomalyVolumeProperties() {
        return AnomalyVolumeProperties.builder()
            .indexTicker(IMOEX)
            .historyPeriod(180)
            .scaleCoefficient(1.5)
            .build();
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
