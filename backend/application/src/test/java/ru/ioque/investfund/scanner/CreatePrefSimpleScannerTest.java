package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.application.modules.scanner.command.UpdateScanner;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;

import java.util.List;

import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

@DisplayName("CREATE PREF SIMPLE SCANNER TEST")
public class CreatePrefSimpleScannerTest extends BaseScannerCommandTest {
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

    private CreateScanner.CreateScannerBuilder buildCreatePrefSimpleScannerWith() {
        return CreateScanner.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .tickers(getTickers())
            .properties(
                getDefaultProperties()
            );
    }

    private UpdateScanner.UpdateScannerBuilder buildUpdatePrefSimpleScannerWith() {
        return UpdateScanner.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .scannerId(getFirstScannerId())
            .tickers(getTickers())
            .properties(
                getDefaultProperties()
            );
    }

    private PrefCommonProperties getDefaultProperties() {
        return PrefCommonProperties.builder()
            .spreadValue(1.0)
            .build();
    }

    private List<Ticker> getTickers() {
        return List.of(new Ticker(TGKN), new Ticker(TGKB), new Ticker(IMOEX));
    }
}
