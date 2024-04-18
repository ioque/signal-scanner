package ru.ioque.investfund.scanner.configurator;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SCANNER MANAGER TEST - ADD NEW SCANNER")
public class CreateConfiguratorTest extends BaseConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команде на создание конфигурации сканера указан тикер несуществующего инструмента.
        """)
    void testCase3() {
        final CreateScannerCommand command = buildCreateAnomalyVolumeScannerWith()
            .instrumentIds(
                Stream
                    .of("TGKN", "LVHK", "TGKM", "IMOEX")
                    .map(ticker -> new InstrumentId(ticker, getDatasourceId())).toList()
            )
            .build();

        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commandBus().execute(command)
        );
        assertEquals(
            "В выбранном источнике данных не существует инструментов с тикерами [LVHK, TGKM].",
            exception.getMessage()
        );
    }

    @Test
    @DisplayName("""
        T2. В команде на создание конфигурации сканера не передано описание.
        """)
    void testCase4() {
        final CreateScannerCommand command = buildCreateAnomalyVolumeScannerWith().description(null).build();

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );

        assertEquals(descIsEmpty(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T3. В команде на создание конфигурации сканера передано пустое описание.
        """)
    void testCase5() {
        final CreateScannerCommand command = buildCreateAnomalyVolumeScannerWith().description("").build();

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );

        assertEquals(descIsEmpty(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T4. В команде на создание конфигурации сканера не передан период работы сканера.
        """)
    void testCase6() {
        final CreateScannerCommand command = buildCreateAnomalyVolumeScannerWith().workPeriodInMinutes(null).build();

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );

        assertEquals(workPeriodIsEmpty(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T5. В команде на создание конфигурации сканера передан период работы сканера равный нулю.
        """)
    void testCase7() {
        final CreateScannerCommand command = buildCreateAnomalyVolumeScannerWith().workPeriodInMinutes(0).build();

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );

        assertEquals(workPeriodIsNegative(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T6. В команде на создание конфигурации сканера передан период работы сканера меньше нуля.
        """)
    void testCase8() {
        final CreateScannerCommand command = buildCreateAnomalyVolumeScannerWith().workPeriodInMinutes(-11).build();

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );

        assertEquals(workPeriodIsNegative(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T7. В команде на создание конфигурации сканера передан пустой список тикеров.
        """)
    void testCase10() {
        final CreateScannerCommand command = buildCreateAnomalyVolumeScannerWith().instrumentIds(List.of()).build();

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );

        assertEquals(instrumentIdsIsEmpty(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T8. В команде на создание конфигурации сканера не передан список тикеров.
        """)
    void testCase11() {
        final CreateScannerCommand command = buildCreateAnomalyVolumeScannerWith().instrumentIds(null).build();

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(command)
        );

        assertEquals(instrumentIdsIsEmpty(), getMessage(exception));
    }
}
