package ru.ioque.investfund.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.domain.configurator.command.SaveScannerCommand;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.core.ValidatorException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANNER CONFIGURATOR TEST - ADD NEW SCANNER")
public class AddNewScannerTest extends BaseScannerConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команде на создание конфигурации сканера передан идентификатор несуществующего источника данных.
        """)
    void testCase1() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().datasourceId(UUID.randomUUID()).build();

        final ApplicationException exception = assertThrows(
            ApplicationException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(
            "Источник данных не найден.",
            exception.getMessage()
        );
    }

    @Test
    @DisplayName("""
        T2. В команде на создание конфигурации сканера не передан идентификатор источника данных.
        """)
    void testCase2() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().datasourceId(null).build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(datasourceIdIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T3. В команде на создание конфигурации сканера указан тикер несуществующего инструмента.
        """)
    void testCase3() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith()
            .tickers(List.of("TGKN", "LVHK", "TGKM", "IMOEX"))
            .build();

        final ValidatorException exception = assertThrows(
            ValidatorException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(2, exception.getErrors().size());
        assertTrue(
            exception.getErrors().containsAll(List.of(
                "Инструмент с тикером LVHK не найден.",
                "Инструмент с тикером TGKM не найден."
            ))
        );
    }

    @Test
    @DisplayName("""
        T4. В команде на создание конфигурации сканера не передано описание.
        """)
    void testCase4() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().description(null).build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(descIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T5. В команде на создание конфигурации сканера передано пустое описание.
        """)
    void testCase5() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().description("").build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(descIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T6. В команде на создание конфигурации сканера не передан период работы сканера.
        """)
    void testCase6() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().workPeriodInMinutes(null).build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(workPeriodIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T7. В команде на создание конфигурации сканера передан период работы сканера равный нулю.
        """)
    void testCase7() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().workPeriodInMinutes(0).build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(workPeriodIsNegative(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T8. В команде на создание конфигурации сканера передан период работы сканера меньше нуля.
        """)
    void testCase8() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().workPeriodInMinutes(-11).build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(workPeriodIsNegative(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T9. В команде на создание конфигурации сканера передан пустой список тикеров.
        """)
    void testCase10() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().tickers(List.of()).build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(tickersIsEmpty(), exception.getMessage());
    }

    @Test
    @DisplayName("""
        T10. В команде на создание конфигурации сканера не передан список тикеров.
        """)
    void testCase11() {
        final SaveScannerCommand command = buildSaveAnomalyVolumeScannerWith().tickers(null).build();

        final DomainException exception = assertThrows(
            DomainException.class,
            () -> scannerConfigurator().addNewScanner(command)
        );

        assertEquals(tickersIsEmpty(), exception.getMessage());
    }
}
