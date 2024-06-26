package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;

import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

@DisplayName("CREATE ANOMALY VOLUME SCANNER TEST")
public class CreateAnomalyVolumeScannerTest extends BaseScannerCommandTest {
    @Test
    @DisplayName("""
        T1. В команду на создание AnomalyVolumeScanner не передан параметр scaleCoefficient.
        """)
    void testCase1() {
        testAddNewScannerError(
            createAnomalyVolumeScannerCommandWith()
                .properties(buildPropertiesWith().scaleCoefficient(null).build())
                .build(),
            scaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T2. В команду на создание AnomalyVolumeScanner не передан параметр historyPeriod.
        """)
    void testCase2() {
        testAddNewScannerError(
            createAnomalyVolumeScannerCommandWith()
                .properties(buildPropertiesWith().historyPeriod(null).build())
                .build(),
            historyPeriodIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T3. В команду на создание AnomalyVolumeScanner не передан параметр indexTicker.
        """)
    void testCase3() {
        testAddNewScannerError(
            createAnomalyVolumeScannerCommandWith()
                .properties(buildPropertiesWith().indexTicker(null).build())
                .build(),
            indexIdIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T4. В команду на создание AnomalyVolumeScanner параметр indexTicker передан как пустая строка.
        """)
    void testCase4() {
        testAddNewScannerError(
            createAnomalyVolumeScannerCommandWith()
                .properties(buildPropertiesWith().indexTicker(new Ticker("")).build())
                .build(),
            indexIdIsNotValid()
        );
    }

    @Test
    @DisplayName("""
        T5. В команду на создание AnomalyVolumeScanner параметр scaleCoefficient передан со значением = 0.
        """)
    void testCase5() {
        testAddNewScannerError(
            createAnomalyVolumeScannerCommandWith()
                .properties(buildPropertiesWith().scaleCoefficient(0D).build())
                .build(),
            scaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T6. В команду на создание AnomalyVolumeScanner параметр scaleCoefficient передан со значением < 0.
        """)
    void testCase6() {
        testAddNewScannerError(
            createAnomalyVolumeScannerCommandWith()
                .properties(buildPropertiesWith().scaleCoefficient(-1D).build())
                .build(),
            scaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T7. В команду на создание AnomalyVolumeScanner параметр historyPeriod передан со значением = 0.
        """)
    void testCase7() {
        testAddNewScannerError(
            createAnomalyVolumeScannerCommandWith()
                .properties(buildPropertiesWith().historyPeriod(0).build())
                .build(),
            historyPeriodIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T8. В команду на создание AnomalyVolumeScanner параметр historyPeriod передан со значением < 0.
        """)
    void testCase8() {
        testAddNewScannerError(
            createAnomalyVolumeScannerCommandWith()
                .properties(buildPropertiesWith().historyPeriod(-180).build())
                .build(),
            historyPeriodIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T9. В команду на обновление AnomalyVolumeScanner не передан параметр scaleCoefficient.
        """)
    void testCase9() {
        commandBus().execute(createAnomalyVolumeScannerCommandWith().build());
        testUpdateScannerError(
            updateAnomalyVolumeScannerCommandWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().scaleCoefficient(null).build())
                .build(),
            scaleCoefficientIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T10. В команду на обновление AnomalyVolumeScanner не передан параметр historyPeriod.
        """)
    void testCase10() {
        commandBus().execute(createAnomalyVolumeScannerCommandWith().build());
        testUpdateScannerError(
            updateAnomalyVolumeScannerCommandWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().historyPeriod(null).build())
                .build(),
            historyPeriodIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T11. В команду на обновление AnomalyVolumeScanner не передан параметр indexTicker.
        """)
    void testCase11() {
        commandBus().execute(createAnomalyVolumeScannerCommandWith().build());
        testUpdateScannerError(
            updateAnomalyVolumeScannerCommandWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().indexTicker(null).build())
                .build(),
            indexIdIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T12. В команду на обновление AnomalyVolumeScanner параметр indexTicker передан как пустая строка.
        """)
    void testCase12() {
        commandBus().execute(createAnomalyVolumeScannerCommandWith().build());
        testUpdateScannerError(
            updateAnomalyVolumeScannerCommandWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().indexTicker(new Ticker("")).build())
                .build(),
            indexIdIsNotValid()
        );
    }

    @Test
    @DisplayName("""
        T13. В команду на обновление AnomalyVolumeScanner параметр scaleCoefficient передан со значением = 0.
        """)
    void testCase13() {
        commandBus().execute(createAnomalyVolumeScannerCommandWith().build());
        testUpdateScannerError(
            updateAnomalyVolumeScannerCommandWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().scaleCoefficient(0D).build())
                .build(),
            scaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T14. В команду на обновление AnomalyVolumeScanner параметр scaleCoefficient передан со значением < 0.
        """)
    void testCase14() {
        commandBus().execute(createAnomalyVolumeScannerCommandWith().build());
        testUpdateScannerError(
            updateAnomalyVolumeScannerCommandWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().scaleCoefficient(-1D).build())
                .build(),
            scaleCoefficientIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T15. В команду на обновление AnomalyVolumeScanner параметр historyPeriod передан со значением = 0.
        """)
    void testCase15() {
        commandBus().execute(createAnomalyVolumeScannerCommandWith().build());
        testUpdateScannerError(
            updateAnomalyVolumeScannerCommandWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().historyPeriod(0).build())
                .build(),
            historyPeriodIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T16. В команду на обновление AnomalyVolumeScanner параметр historyPeriod передан со значением < 0.
        """)
    void testCase16() {
        commandBus().execute(createAnomalyVolumeScannerCommandWith().build());
        testUpdateScannerError(
            updateAnomalyVolumeScannerCommandWith()
                .scannerId(getFirstScannerId())
                .properties(buildPropertiesWith().historyPeriod(-180).build())
                .build(),
            historyPeriodIsNegative()
        );
    }

    private String historyPeriodIsEmpty() {
        return "Не передан параметр historyPeriod.";
    }

    private String scaleCoefficientIsEmpty() {
        return "Не передан параметр scaleCoefficient.";
    }
    
    private String indexIdIsEmpty() {
        return "Не передан идентификатор индекса.";
    }

    private String indexIdIsNotValid() {
        return "Тикер должен быть непустой строкой, состоящей из латинских букв или цифр.";
    }

    private String scaleCoefficientIsNegative() {
        return "Параметр scaleCoefficient должен быть больше 0.";
    }
    
    private String historyPeriodIsNegative() {
        return "Параметр historyPeriod должен быть больше 0.";
    }

    private AnomalyVolumeProperties.AnomalyVolumePropertiesBuilder buildPropertiesWith() {
        return AnomalyVolumeProperties.builder()
            .historyPeriod(180)
            .indexTicker(Ticker.from(IMOEX))
            .scaleCoefficient(1.5);
    }
}
