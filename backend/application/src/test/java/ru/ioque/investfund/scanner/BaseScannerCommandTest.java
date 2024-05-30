package ru.ioque.investfund.scanner;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.application.modules.scanner.command.UpdateScanner;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

public class BaseScannerCommandTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        prepareDatasource();
        loggerProvider().clearLogs();
    }

    protected ScannerId getFirstScannerId() {
        return scannerRepository().getScanners().keySet().iterator().next();
    }

    protected SignalScanner getScanner(ScannerId scannerId) {
        return scannerRepository().findBy(scannerId).orElseThrow();
    }

    private void prepareDatasource() {
        datasourceStorage().initInstrumentDetails(
            List.of(
                instrumentFixture.imoexDetails(),
                instrumentFixture.tgkbDetails(),
                instrumentFixture.tgknDetails(),
                instrumentFixture.sber(),
                instrumentFixture.sberp(),
                instrumentFixture.rosnDetails(),
                instrumentFixture.sibn(),
                instrumentFixture.lkohDetails(),
                instrumentFixture.tatnDetails(),
                instrumentFixture.brf4(),
                instrumentFixture.usdRubDetails())
        );
        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));
        commandBus().execute(new EnableUpdateInstruments(getDatasourceId(), getTickers(getDatasourceId())));
    }

    protected void testAddNewScannerError(CreateScanner command, String msg) {
        var error = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );
        assertEquals(msg, getMessage(error));
    }

    protected void testUpdateScannerError(UpdateScanner command, String msg) {
        var error = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );
        assertEquals(msg, getMessage(error));
    }

    protected CreateScanner.CreateScannerBuilder createAnomalyVolumeScannerCommandWith() {
        return CreateScanner.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(getAnomalyVolumeTickers())
            .properties(
                getDefaultAnomalyVolumeProperties()
            );
    }

    protected UpdateScanner.UpdateScannerBuilder updateAnomalyVolumeScannerCommandWith() {
        return UpdateScanner.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .tickers(getAnomalyVolumeTickers())
            .properties(
                getDefaultAnomalyVolumeProperties()
            );
    }

    private List<@Valid Ticker> getAnomalyVolumeTickers() {
        return List.of(new Ticker(TGKN), new Ticker(TGKB), new Ticker(IMOEX));
    }

    private AnomalyVolumeProperties getDefaultAnomalyVolumeProperties() {
        return AnomalyVolumeProperties.builder()
            .indexTicker(new Ticker(IMOEX))
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
