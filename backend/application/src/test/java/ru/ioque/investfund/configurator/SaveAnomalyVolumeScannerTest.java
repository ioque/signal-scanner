package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SaveAnomalyVolumeScannerTest extends BaseScannerConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команду на создание AnomalyVolumeScanner не передан параметр scaleCoefficient.
        """)
    void testCase1() {
        testAddNewScannerError(
            buildSaveAnomalyVolumeScannerWith().scaleCoefficient(null).build(),
            scaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T2. В команду на создание AnomalyVolumeScanner не передан параметр historyPeriod.
        """)
    void testCase2() {
        testAddNewScannerError(
            buildSaveAnomalyVolumeScannerWith().historyPeriod(null).build(),
            historyPeriodIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T3. В команду на создание AnomalyVolumeScanner не передан параметр indexTicker.
        """)
    void testCase3() {
        testAddNewScannerError(
            buildSaveAnomalyVolumeScannerWith().indexTicker(null).build(),
            indexTickerIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T4. В команду на создание AnomalyVolumeScanner параметр indexTicker передан как пустая строка.
        """)
    void testCase4() {
        testAddNewScannerError(
            buildSaveAnomalyVolumeScannerWith().indexTicker("").build(),
            indexTickerIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T5. В команду на создание AnomalyVolumeScanner параметр scaleCoefficient передан со значением = 0.
        """)
    void testCase5() {
        testAddNewScannerError(
            buildSaveAnomalyVolumeScannerWith().scaleCoefficient(0D).build(),
            scaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T6. В команду на создание AnomalyVolumeScanner параметр scaleCoefficient передан со значением < 0.
        """)
    void testCase6() {
        testAddNewScannerError(
            buildSaveAnomalyVolumeScannerWith().scaleCoefficient(-1D).build(),
            scaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T7. В команду на создание AnomalyVolumeScanner параметр historyPeriod передан со значением = 0.
        """)
    void testCase7() {
        testAddNewScannerError(
            buildSaveAnomalyVolumeScannerWith().historyPeriod(0).build(),
            historyPeriodIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T8. В команду на создание AnomalyVolumeScanner параметр historyPeriod передан со значением < 0.
        """)
    void testCase8() {
        testAddNewScannerError(
            buildSaveAnomalyVolumeScannerWith().historyPeriod(-180).build(),
            historyPeriodIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T9. В команду на обновление AnomalyVolumeScanner не передан параметр scaleCoefficient.
        """)
    void testCase9() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith().scaleCoefficient(null).build(),
            scaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T10. В команду на обновление AnomalyVolumeScanner не передан параметр historyPeriod.
        """)
    void testCase10() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith().historyPeriod(null).build(),
            historyPeriodIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T11. В команду на обновление AnomalyVolumeScanner не передан параметр indexTicker.
        """)
    void testCase11() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith().indexTicker(null).build(),
            indexTickerIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T12. В команду на обновление AnomalyVolumeScanner параметр indexTicker передан как пустая строка.
        """)
    void testCase12() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith().indexTicker("").build(),
            indexTickerIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T13. В команду на обновление AnomalyVolumeScanner параметр scaleCoefficient передан со значением = 0.
        """)
    void testCase13() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith().scaleCoefficient(0D).build(),
            scaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T14. В команду на обновление AnomalyVolumeScanner параметр scaleCoefficient передан со значением < 0.
        """)
    void testCase14() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith().scaleCoefficient(-1D).build(),
            scaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T15. В команду на обновление AnomalyVolumeScanner параметр historyPeriod передан со значением = 0.
        """)
    void testCase15() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith().historyPeriod(0).build(),
            historyPeriodIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T16. В команду на обновление AnomalyVolumeScanner параметр historyPeriod передан со значением < 0.
        """)
    void testCase16() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith().historyPeriod(-180).build(),
            historyPeriodIsNegative()
        );
    }

    private String historyPeriodIsEmpty() {
        return "Не передан параметр historyPeriod.";
    }

    private String scaleCoefficientIsEmpty() {
        return "Не передан параметр scaleCoefficient.";
    }
    
    private String indexTickerIsEmpty() {
        return "Не передан параметр indexTicker.";
    }

    private String scaleCoefficientIsNegative() {
        return "Параметр scaleCoefficient должен быть больше нуля.";
    }
    
    private String historyPeriodIsNegative() {
        return "Параметр historyPeriod должен быть больше нуля.";
    }
}
