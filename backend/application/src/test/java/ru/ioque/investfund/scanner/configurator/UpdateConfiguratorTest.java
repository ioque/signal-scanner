package ru.ioque.investfund.scanner.configurator;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SCANNER MANAGER TEST - UPDATE SCANNER")
public class UpdateConfiguratorTest extends BaseConfiguratorTest {
    @Test
    @DisplayName("""
        T1. Попытка обновить конфигурацию несуществующего сканера.
        """)
    void testCase1() {
        final ScannerId scannerId = ScannerId.from(UUID.randomUUID());
        final EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(buildUpdateAnomalyVolumeScannerWith().scannerId(scannerId).build())
        );

        assertEquals(
            String.format("Сканер[id=%s] не существует.", scannerId),
            exception.getMessage()
        );
    }

    @Test
    @DisplayName("""
        T2. В системе сохранен сканер со следующим списком тикеров: TGKN, TGKB, IMOEX.
        Поступила команда на обновление сканера, изменился список тикеров: TGKN, IMOEX.
        """)
    void testCase2() {
        commandBus().execute(
            buildCreateAnomalyVolumeScannerWith()
                .tickers(List.of(new Ticker(TGKN), new Ticker(TGKB), new Ticker(IMOEX)))
                .build()
        );
        final ScannerId scannerId = getFirstScannerId();

        commandBus().execute(
            buildUpdateAnomalyVolumeScannerWith()
                .scannerId(scannerId)
                .tickers(List.of(new Ticker(TGKN), new Ticker(IMOEX)))
                .build()
        );
        assertEquals(2, getScanner(scannerId).getInstrumentIds().size());
    }

    @Test
    @DisplayName("""
        T3. В команде на обновление сканера не указано описание.
        """)
    void testCase4() {
        commandBus().execute(buildCreateAnomalyVolumeScannerWith().build());

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                buildUpdateAnomalyVolumeScannerWith()
                    .scannerId(getFirstScannerId())
                    .description(null)
                    .build()
            )
        );

        assertEquals(descIsEmpty(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T5. В команде на обновление сканера указано пустое описание.
        """)
    void testCase5() {
        commandBus().execute(buildCreateAnomalyVolumeScannerWith().build());

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                buildUpdateAnomalyVolumeScannerWith()
                    .scannerId(getFirstScannerId())
                    .description("")
                    .build()
            )
        );

        assertEquals(descIsEmpty(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T6. В команде на обновление сканера не передан период работы.
        """)
    void testCase6() {
        commandBus().execute(buildCreateAnomalyVolumeScannerWith().build());

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                buildUpdateAnomalyVolumeScannerWith()
                    .scannerId(getFirstScannerId())
                    .workPeriodInMinutes(null)
                    .build()
            )
        );

        assertEquals(workPeriodIsEmpty(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T7. В команде на обновление сканера передан период работы равный нулю.
        """)
    void testCase7() {
        commandBus().execute(buildCreateAnomalyVolumeScannerWith().build());

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                buildUpdateAnomalyVolumeScannerWith()
                    .scannerId(getFirstScannerId())
                    .workPeriodInMinutes(0)
                    .build()
            )
        );

        assertEquals(workPeriodIsNegative(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T8. В команде на обновление сканера передан период работы меньше нуля.
        """)
    void testCase8() {
        commandBus().execute(buildCreateAnomalyVolumeScannerWith().build());

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                buildUpdateAnomalyVolumeScannerWith()
                    .scannerId(getFirstScannerId())
                    .workPeriodInMinutes(-11)
                    .build()
            )
        );

        assertEquals(workPeriodIsNegative(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T9. В команде на обновление сканера не передан список тикеров анализируемых инструментов.
        """)
    void testCase10() {
        commandBus().execute(buildCreateAnomalyVolumeScannerWith().build());

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                buildUpdateAnomalyVolumeScannerWith()
                    .scannerId(getFirstScannerId())
                    .tickers(null)
                    .build()
            )
        );

        assertEquals(tickersIsEmpty(), getMessage(exception));
    }

    @Test
    @DisplayName("""
        T10. В команде на обновление сканера передан пустой список тикеров анализируемых инструментов.
        """)
    void testCase11() {
        commandBus().execute(buildCreateAnomalyVolumeScannerWith().build());

        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                buildUpdateAnomalyVolumeScannerWith()
                    .scannerId(getFirstScannerId())
                    .tickers(List.of())
                    .build()
            )
        );

        assertEquals(tickersIsEmpty(), getMessage(exception));
    }
}
