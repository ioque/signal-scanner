package ru.ioque.investfund.scanner.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;

import java.util.stream.Stream;

@DisplayName("SCANNER MANAGER TEST - SAVE SIMPLE PREF SCANNER")
public class PrefSimpleConfiguratorTest extends BaseConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команду на создание PrefSimpleScanner не передан параметр spreadParam.
        """)
    void testCase1() {
        testAddNewScannerError(
            buildCreatePrefSimpleScannerWith().properties(buildPropertiesWith().spreadValue(null).build()).build(),
            spreadParamIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T2. В команду на создание PrefSimpleScanner параметр spreadParam передан со значением = 0.
        """)
    void testCase2() {
        testAddNewScannerError(
            buildCreatePrefSimpleScannerWith().properties(buildPropertiesWith().spreadValue(0D).build()).build(),
            spreadParamIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T3. В команду на создание PrefSimpleScanner параметр spreadParam передан со значением < 0.
        """)
    void testCase3() {
        testAddNewScannerError(
            buildCreatePrefSimpleScannerWith().properties(buildPropertiesWith().spreadValue(-1D).build()).build(),
            spreadParamIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T4. В команду на обновление PrefSimpleScanner не передан параметр spreadParam.
        """)
    void testCase4() {
        commandBus().execute(buildCreatePrefSimpleScannerWith().build());
        testUpdateScannerError(
            buildUpdatePrefSimpleScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().spreadValue(null).build())
                .build(),
            spreadParamIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T5. В команду на обновление PrefSimpleScanner параметр spreadParam передан со значением = 0.
        """)
    void testCase5() {
        commandBus().execute(buildCreatePrefSimpleScannerWith().build());
        testUpdateScannerError(
            buildUpdatePrefSimpleScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().spreadValue(0D).build())
                .build(),
            spreadParamIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T6. В команду на обновление PrefSimpleScanner параметр spreadParam передан со значением < 0.
        """)
    void testCase6() {
        commandBus().execute(buildCreatePrefSimpleScannerWith().build());
        testUpdateScannerError(
            buildUpdatePrefSimpleScannerWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().spreadValue(-1D).build())
                .build(),
            spreadParamIsNegative()
        );
    }

    private String spreadParamIsEmpty() {
        return "Не передан параметр spreadValue.";
    }

    private String spreadParamIsNegative() {
        return "Параметр spreadValue должен быть больше 0.";
    }

    private PrefCommonProperties.PrefCommonPropertiesBuilder buildPropertiesWith() {
        return PrefCommonProperties.builder()
            .spreadValue(1.0);
    }

    private CreateScannerCommand.CreateScannerCommandBuilder buildCreatePrefSimpleScannerWith() {
        return CreateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .instrumentIds(Stream.of("TGKN", "TGKB", "IMOEX").map(ticker -> new InstrumentId(ticker, getDatasourceId())).toList())
            .properties(
                PrefCommonProperties.builder()
                    .spreadValue(1.0)
                    .build()
            );
    }

    private UpdateScannerCommand.UpdateScannerCommandBuilder buildUpdatePrefSimpleScannerWith() {
        return UpdateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .scannerId(getFirstScannerId())
            .instrumentIds(Stream.of("TGKN", "TGKB", "IMOEX").map(ticker -> new InstrumentId(ticker, getDatasourceId())).toList())
            .properties(
                PrefCommonProperties.builder()
                    .spreadValue(1.0)
                    .build()
            );
    }
}
