package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SCANNER CONFIGURATOR TEST - SAVE SECTORAL RETARD SCANNER")
public class SaveSectoralRetardScannerTest extends BaseScannerConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команду на создание SectoralRetardScanner не передан параметр historyScale.
        """)
    void testCase1() {
        testAddNewScannerError(
            buildSaveSectoralRetardScannerWith().historyScale(null).build(),
            historyScaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T2. В команду на создание SectoralRetardScanner параметр historyScale передан со значением = 0.
        """)
    void testCase2() {
        testAddNewScannerError(
            buildSaveSectoralRetardScannerWith().historyScale(0D).build(),
            historyScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T3. В команду на создание SectoralRetardScanner параметр historyScale передан со значением < 0.
        """)
    void testCase3() {
        testAddNewScannerError(
            buildSaveSectoralRetardScannerWith().historyScale(-1D).build(),
            historyScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T4. В команду на создание SectoralRetardScanner не передан параметр intradayScale.
        """)
    void testCase4() {
        testAddNewScannerError(
            buildSaveSectoralRetardScannerWith().intradayScale(null).build(),
            intradayScaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T5. В команду на создание SectoralRetardScanner параметр intradayScale передан со значением = 0.
        """)
    void testCase5() {
        testAddNewScannerError(
            buildSaveSectoralRetardScannerWith().intradayScale(0D).build(),
            intradayScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T6. В команду на создание SectoralRetardScanner параметр intradayScale передан со значением < 0.
        """)
    void testCase6() {
        testAddNewScannerError(
            buildSaveSectoralRetardScannerWith().intradayScale(-1D).build(),
            intradayScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T7. В команду на обновление SectoralRetardScanner не передан параметр historyScale.
        """)
    void testCase7() {
        scannerConfigurator().addNewScanner(buildSaveSectoralRetardScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralRetardScannerWith().historyScale(null).build(),
            historyScaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T8. В команду на обновление SectoralRetardScanner параметр historyScale передан со значением = 0.
        """)
    void testCase8() {
        scannerConfigurator().addNewScanner(buildSaveSectoralRetardScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralRetardScannerWith().historyScale(0D).build(),
            historyScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T9. В команду на обновление SectoralRetardScanner параметр historyScale передан со значением < 0.
        """)
    void testCase9() {
        scannerConfigurator().addNewScanner(buildSaveSectoralRetardScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralRetardScannerWith().historyScale(-1D).build(),
            historyScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T10. В команду на обновление SectoralRetardScanner не передан параметр intradayScale.
        """)
    void testCase10() {
        scannerConfigurator().addNewScanner(buildSaveSectoralRetardScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralRetardScannerWith().intradayScale(null).build(),
            intradayScaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T11. В команду на обновление SectoralRetardScanner параметр intradayScale передан со значением = 0.
        """)
    void testCase11() {
        scannerConfigurator().addNewScanner(buildSaveSectoralRetardScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralRetardScannerWith().intradayScale(0D).build(),
            intradayScaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T12. В команду на обновление SectoralRetardScanner параметр intradayScale передан со значением < 0.
        """)
    void testCase12() {
        scannerConfigurator().addNewScanner(buildSaveSectoralRetardScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralRetardScannerWith().intradayScale(-1D).build(),
            intradayScaleCoefficientIsNegative()
        );
    }

    private String intradayScaleCoefficientIsNegative() {
        return "Параметр intradayScale должен быть больше нуля.";
    }

    private String intradayScaleCoefficientIsEmpty() {
        return "Не передан параметр intradayScale.";
    }

    private String historyScaleCoefficientIsNegative() {
        return "Параметр historyScale должен быть больше нуля.";
    }

    private String historyScaleCoefficientIsEmpty() {
        return "Не передан параметр historyScale.";
    }
}
