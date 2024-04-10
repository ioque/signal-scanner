package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.domain.core.DomainException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANNER CONFIGURATOR TEST - UPDATE SCANNER")
public class UpdateScannerTest extends BaseScannerConfiguratorTest {
    @Test
    @DisplayName("""
        T1. Попытка обновить конфигурацию несуществующего сканера.
        """)
    void testCase1() {
        final UUID scannerId = UUID.randomUUID();
        final ApplicationException exception = assertThrows(
            ApplicationException.class,
            () -> scannerConfigurator().updateScanner(scannerId, buildSaveAnomalyVolumeScannerWith().build())
        );

        assertEquals(
            "Сканер сигналов с идентификатором " + scannerId + " не найден.",
            exception.getMessage()
        );
    }

    @Test
    @DisplayName("""
        T2. В системе сохранен сканер со следующим списком тикеров: TGKN, TGKB, IMOEX.
        Поступила команда на обновление сканера, изменился список тикеров: TGKN, IMOEX.
        """)
    void testCase2() {
        scannerConfigurator().addNewScanner(
            buildSaveAnomalyVolumeScannerWith()
                .tickers(List.of("TGKN", "TGKB", "IMOEX"))
                .build()
        );

        scannerConfigurator().updateScanner(
            getScannerId(),
            buildSaveAnomalyVolumeScannerWith()
                .tickers(List.of("TGKN", "IMOEX"))
                .build()
        );

        assertTrue(List.of("TGKN", "IMOEX").containsAll(getScanner().getTickers()));
    }

    @Test
    @DisplayName("""
        T3. В команде на обновление сканера не указан источник данных.
        """)
    void testCase3() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator()
                .updateScanner(
                    getScannerId(),
                    buildSaveAnomalyVolumeScannerWith()
                        .datasourceId(null)
                        .build()
                )
        );

        assertEquals(datasourceIdIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T4. В команде на обновление сканера не указано описание.
        """)
    void testCase4() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator()
                .updateScanner(
                    getScannerId(),
                    buildSaveAnomalyVolumeScannerWith()
                        .description(null)
                        .build()
                )
        );

        assertEquals(descIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T5. В команде на обновление сканера указано пустое описание.
        """)
    void testCase5() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator()
                .updateScanner(
                    getScannerId(),
                    buildSaveAnomalyVolumeScannerWith()
                        .description("")
                        .build()
                )
        );

        assertEquals(descIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T6. В команде на обновление сканера не передан период работы.
        """)
    void testCase6() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator()
                .updateScanner(
                    getScannerId(),
                    buildSaveAnomalyVolumeScannerWith()
                        .workPeriodInMinutes(null)
                        .build()
                )
        );

        assertEquals(workPeriodIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T7. В команде на обновление сканера передан период работы равный нулю.
        """)
    void testCase7() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator()
                .updateScanner(
                    getScannerId(),
                    buildSaveAnomalyVolumeScannerWith()
                        .workPeriodInMinutes(0)
                        .build()
                )
        );

        assertEquals(workPeriodIsNegative(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T8. В команде на обновление сканера передан период работы меньше нуля.
        """)
    void testCase8() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator()
                .updateScanner(
                    getScannerId(),
                    buildSaveAnomalyVolumeScannerWith()
                        .workPeriodInMinutes(-11)
                        .build()
                )
        );

        assertEquals(workPeriodIsNegative(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T9. В команде на обновление сканера не передан список тикеров анализируемых инструментов.
        """)
    void testCase10() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator()
                .updateScanner(
                    getScannerId(),
                    buildSaveAnomalyVolumeScannerWith()
                        .tickers(null)
                        .build()
                )
        );

        assertEquals(tickersIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T10. В команде на обновление сканера передан пустой список тикеров анализируемых инструментов.
        """)
    void testCase11() {
        scannerConfigurator().addNewScanner(buildSaveAnomalyVolumeScannerWith().build());

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator()
                .updateScanner(
                    getScannerId(),
                    buildSaveAnomalyVolumeScannerWith()
                        .tickers(List.of())
                        .build()
                )
        );

        assertEquals(tickersIsEmpty(), exception.getMessage());
    }
}
