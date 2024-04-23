package ru.ioque.investfund.scanner.configurator;

import jakarta.validation.Valid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.application.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.application.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;

import java.util.List;

@DisplayName("SCANNER MANAGER TEST - SAVE SECTORAL RETARD SCANNER")
public class SectoralRetardConfiguratorTest extends BaseConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команду на создание SectoralRetardScanner не передан параметр historyScale.
        """)
    void testCase1() {
        testAddNewScannerError(
            buildCreateSectoralRetardScannerWith()
                .properties(buildPropertiesWith().historyScale(null).build())
                .build(),
            historyScaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T2. В команду на создание SectoralRetardScanner параметр historyScale передан со значением = 0.
        """)
    void testCase2() {
        testAddNewScannerError(
            buildCreateSectoralRetardScannerWith()
                .properties(buildPropertiesWith().historyScale(0D).build())
                .build(),
            historyScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T3. В команду на создание SectoralRetardScanner параметр historyScale передан со значением < 0.
        """)
    void testCase3() {
        testAddNewScannerError(
            buildCreateSectoralRetardScannerWith()
                .properties(buildPropertiesWith().historyScale(-1D).build())
                .build(),
            historyScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T4. В команду на создание SectoralRetardScanner не передан параметр intradayScale.
        """)
    void testCase4() {
        testAddNewScannerError(
            buildCreateSectoralRetardScannerWith()
                .properties(buildPropertiesWith().intradayScale(null).build())
                .build(),
            intradayScaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T5. В команду на создание SectoralRetardScanner параметр intradayScale передан со значением = 0.
        """)
    void testCase5() {
        testAddNewScannerError(
            buildCreateSectoralRetardScannerWith()
                .properties(buildPropertiesWith().intradayScale(0D).build())
                .build(),
            intradayScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T6. В команду на создание SectoralRetardScanner параметр intradayScale передан со значением < 0.
        """)
    void testCase6() {
        testAddNewScannerError(
            buildCreateSectoralRetardScannerWith()
                .properties(buildPropertiesWith().intradayScale(-1D).build())
                .build(),
            intradayScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T7. В команду на обновление SectoralRetardScanner не передан параметр historyScale.
        """)
    void testCase7() {
        commandBus().execute(buildCreateSectoralRetardScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralRetardScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().historyScale(null).build())
                .build(),
            historyScaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T8. В команду на обновление SectoralRetardScanner параметр historyScale передан со значением = 0.
        """)
    void testCase8() {
        commandBus().execute(buildCreateSectoralRetardScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralRetardScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().historyScale(0D).build())
                .build(),
            historyScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T9. В команду на обновление SectoralRetardScanner параметр historyScale передан со значением < 0.
        """)
    void testCase9() {
        commandBus().execute(buildCreateSectoralRetardScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralRetardScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().historyScale(-1D).build())
                .build(),
            historyScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T10. В команду на обновление SectoralRetardScanner не передан параметр intradayScale.
        """)
    void testCase10() {
        commandBus().execute(buildCreateSectoralRetardScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralRetardScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().intradayScale(null).build())
                .build(),
            intradayScaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T11. В команду на обновление SectoralRetardScanner параметр intradayScale передан со значением = 0.
        """)
    void testCase11() {
        commandBus().execute(buildCreateSectoralRetardScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralRetardScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().intradayScale(0D).build())
                .build(),
            intradayScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T12. В команду на обновление SectoralRetardScanner параметр intradayScale передан со значением < 0.
        """)
    void testCase12() {
        commandBus().execute(buildCreateSectoralRetardScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralRetardScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().intradayScale(-1D).build())
                .build(),
            intradayScaleCoefficientIsNegative()
        );
    }

    private String intradayScaleCoefficientIsNegative() {
        return "Параметр intradayScale должен быть больше 0.";
    }

    private String intradayScaleCoefficientIsEmpty() {
        return "Не передан параметр intradayScale.";
    }

    private String historyScaleCoefficientIsNegative() {
        return "Параметр historyScale должен быть больше 0.";
    }

    private String historyScaleCoefficientIsEmpty() {
        return "Не передан параметр historyScale.";
    }

    private SectoralRetardProperties.SectoralRetardPropertiesBuilder buildPropertiesWith() {
        return SectoralRetardProperties.builder()
            .intradayScale(0.015)
            .historyScale(0.015);
    }

    private CreateScannerCommand.CreateScannerCommandBuilder buildCreateSectoralRetardScannerWith() {
        return CreateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(getTickers())
            .properties(getDefaultProperties());
    }

    private UpdateScannerCommand.UpdateScannerCommandBuilder buildUpdateSectoralRetardScannerWith() {
        return UpdateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .scannerId(getFirstScannerId())
            .tickers(getTickers())
            .properties(getDefaultProperties());
    }

    private SectoralRetardProperties getDefaultProperties() {
        return SectoralRetardProperties.builder()
            .historyScale(0.015)
            .intradayScale(0.015)
            .build();
    }

    private List<@Valid Ticker> getTickers() {
        return List.of(new Ticker(TGKN), new Ticker(TGKB), new Ticker(IMOEX));
    }
}
