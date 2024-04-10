package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SCANNER CONFIGURATOR TEST - SAVE SECTORAL CORRELATION SCANNER")
public class SaveCorrelationScannerTest extends BaseScannerConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команду на создание CorrelationSectoralScanner не передан параметр futuresOvernightScale.
        """)
    void testCase1() {
        testAddNewScannerError(
            buildSaveSectoralCorrelationScannerWith().futuresOvernightScale(null).build(),
            futuresOvernightScaleIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T2. В команду на создание CorrelationSectoralScanner не передан параметр stockOvernightScale.
        """)
    void testCase2() {
        testAddNewScannerError(
            buildSaveSectoralCorrelationScannerWith().stockOvernightScale(null).build(),
            stockOvernightScaleIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T3. В команду на создание CorrelationSectoralScanner не передан параметр futuresTicker.
        """)
    void testCase3() {
        testAddNewScannerError(
            buildSaveSectoralCorrelationScannerWith().futuresTicker(null).build(),
            futuresTickerIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T4. В команду на создание CorrelationSectoralScanner параметр futuresTicker передан как пустая строка.
        """)
    void testCase4() {
        testAddNewScannerError(
            buildSaveSectoralCorrelationScannerWith().futuresTicker("").build(),
            futuresTickerIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T5. В команду на создание CorrelationSectoralScanner параметр futuresOvernightScale передан со значением = 0.
        """)
    void testCase5() {
        testAddNewScannerError(
            buildSaveSectoralCorrelationScannerWith().futuresOvernightScale(0D).build(),
            futuresOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T6. В команду на создание CorrelationSectoralScanner параметр futuresOvernightScale передан со значением < 0.
        """)
    void testCase6() {
        testAddNewScannerError(
            buildSaveSectoralCorrelationScannerWith().futuresOvernightScale(-1D).build(),
            futuresOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T7. В команду на создание CorrelationSectoralScanner параметр stockOvernightScale передан со значением = 0.
        """)
    void testCase7() {
        testAddNewScannerError(
            buildSaveSectoralCorrelationScannerWith().stockOvernightScale(-1D).build(),
            stockOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T8. В команду на создание CorrelationSectoralScanner параметр stockOvernightScale передан со значением < 0.
        """)
    void testCase8() {
        testAddNewScannerError(
            buildSaveSectoralCorrelationScannerWith().stockOvernightScale(-1D).build(),
            stockOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T9. В команду на обновление CorrelationSectoralScanner не передан параметр futuresOvernightScale.
        """)
    void testCase9() {
        scannerConfigurator().addNewScanner(buildSaveSectoralCorrelationScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralCorrelationScannerWith().futuresOvernightScale(null).build(),
            futuresOvernightScaleIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T10. В команду на обновление CorrelationSectoralScanner не передан параметр stockOvernightScale.
        """)
    void testCase10() {
        scannerConfigurator().addNewScanner(buildSaveSectoralCorrelationScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralCorrelationScannerWith().stockOvernightScale(null).build(),
            stockOvernightScaleIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T11. В команду на обновление CorrelationSectoralScanner не передан параметр futuresTicker.
        """)
    void testCase11() {
        scannerConfigurator().addNewScanner(buildSaveSectoralCorrelationScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralCorrelationScannerWith().futuresTicker(null).build(),
            futuresTickerIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T12. В команду на обновление CorrelationSectoralScanner параметр futuresTicker передан как пустая строка.
        """)
    void testCase12() {
        scannerConfigurator().addNewScanner(buildSaveSectoralCorrelationScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralCorrelationScannerWith().futuresTicker("").build(),
            futuresTickerIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T13. В команду на обновление CorrelationSectoralScanner параметр futuresOvernightScale передан со значением = 0.
        """)
    void testCase13() {
        scannerConfigurator().addNewScanner(buildSaveSectoralCorrelationScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralCorrelationScannerWith().futuresOvernightScale(0D).build(),
            futuresOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T14. В команду на обновление CorrelationSectoralScanner параметр futuresOvernightScale передан со значением < 0.
        """)
    void testCase14() {
        scannerConfigurator().addNewScanner(buildSaveSectoralCorrelationScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralCorrelationScannerWith().futuresOvernightScale(-1D).build(),
            futuresOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T15. В команду на обновление CorrelationSectoralScanner параметр stockOvernightScale передан со значением = 0.
        """)
    void testCase15() {
        scannerConfigurator().addNewScanner(buildSaveSectoralCorrelationScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralCorrelationScannerWith().stockOvernightScale(-1D).build(),
            stockOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T16. В команду на обновление CorrelationSectoralScanner параметр stockOvernightScale передан со значением < 0.
        """)
    void testCase16() {
        scannerConfigurator().addNewScanner(buildSaveSectoralCorrelationScannerWith().build());
        testUpdateScannerError(
            getScannerId(),
            buildSaveSectoralCorrelationScannerWith().stockOvernightScale(-1D).build(),
            stockOvernightScaleIsNegative()
        );
    }

    private String futuresOvernightScaleIsEmpty() {
        return "Не передан параметр futuresOvernightScale.";
    }
    private String stockOvernightScaleIsEmpty() {
        return "Не передан параметр stockOvernightScale.";
    }

    private String futuresTickerIsEmpty() {
        return "Не передан параметр futuresTicker.";
    }

    private String stockOvernightScaleIsNegative() {
        return "Параметр stockOvernightScale должен быть больше нуля.";
    }

    private String futuresOvernightScaleIsNegative() {
        return "Параметр futuresOvernightScale должен быть больше нуля.";
    }
}
