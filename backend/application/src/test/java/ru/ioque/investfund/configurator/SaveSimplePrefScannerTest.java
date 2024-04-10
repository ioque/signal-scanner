package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SCANNER CONFIGURATOR TEST - SAVE SIMPLE PREF SCANNER")
public class SaveSimplePrefScannerTest extends BaseScannerConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команду на создание PrefSimpleScanner не передан параметр spreadParam.
        """)
    void testCase1() {
        testAddNewScannerError(
            buildSavePrefSimpleScannerWith().spreadParam(null).build(),
            spreadParamIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T2. В команду на создание PrefSimpleScanner параметр spreadParam передан со значением = 0.
        """)
    void testCase2() {
        testAddNewScannerError(
            buildSavePrefSimpleScannerWith().spreadParam(0D).build(),
            spreadParamIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T3. В команду на создание PrefSimpleScanner параметр spreadParam передан со значением < 0.
        """)
    void testCase3() {
        testAddNewScannerError(
            buildSavePrefSimpleScannerWith().spreadParam(-1D).build(),
            spreadParamIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T4. В команду на обновление PrefSimpleScanner не передан параметр spreadParam.
        """)
    void testCase4() {
        scannerConfigurator().addNewScanner(buildSavePrefSimpleScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSavePrefSimpleScannerWith().spreadParam(null).build(),
            spreadParamIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T5. В команду на обновление PrefSimpleScanner параметр spreadParam передан со значением = 0.
        """)
    void testCase5() {
        scannerConfigurator().addNewScanner(buildSavePrefSimpleScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSavePrefSimpleScannerWith().spreadParam(0D).build(),
            spreadParamIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T6. В команду на обновление PrefSimpleScanner параметр spreadParam передан со значением < 0.
        """)
    void testCase6() {
        scannerConfigurator().addNewScanner(buildSavePrefSimpleScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSavePrefSimpleScannerWith().spreadParam(-1D).build(),
            spreadParamIsNegative()
        );
    }

    private String spreadParamIsEmpty() {
        return "Не передан параметр spreadParam.";
    }
    private String spreadParamIsNegative() {
        return "Параметр spreadParam должен быть больше нуля.";
    }
}
