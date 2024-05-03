package ru.ioque.investfund.risk;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.application.modules.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.application.modules.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.stream.Stream;

public abstract class RiskManagerTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        datasourceStorage().initInstrumentDetails(
            List.of(
                imoex(),
                tgkbDetails(),
                tgknDetails()
            )
        );
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        commandBus().execute(
            CreateScannerCommand.builder()
                .workPeriodInMinutes(1)
                .description("Аномальные объемы, третий эшелон.")
                .datasourceId(getDatasourceId())
                .tickers(Stream.of(TGKN, TGKB, IMOEX).map(Ticker::from).toList())
                .properties(
                    AnomalyVolumeProperties.builder()
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(2)
                        .scaleCoefficient(1.5)
                        .build()
                )
                .build()
        );
        loggerProvider().clearLogs();
    }

    protected ScannerId getScannerId() {
        return scannerRepository().getScannerMap().values().stream().findFirst().orElseThrow().getId();
    }
}
