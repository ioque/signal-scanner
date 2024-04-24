package ru.ioque.investfund.risk;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.application.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.application.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.application.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.application.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("RISK COMMAND VALIDATION TEST")
public class RiskCommandValidationTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        datasourceStorage().initInstrumentDetails(
            List.of(
                imoex(),
                tgkbDetails(),
                tgknDetails()
            )
        );
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        commandBus().execute(
            CreateScannerCommand.builder()
                .workPeriodInMinutes(1)
                .description("Аномальные объемы, третий эшелон.")
                .datasourceId(getDatasourceId())
                .tickers(Stream.of(TGKN, TGKB, IMOEX).map(Ticker::from).toList())
                .properties(
                    AnomalyVolumeProperties.builder()
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(2)
                        .scaleCoefficient(1.5)
                        .build()
                )
                .build()
        );
        loggerProvider().clearLogs();
    }

    private InstrumentId getInstrumentIdBy(String ticker) {
        return datasourceRepository().getInstrumentBy(Ticker.from(ticker)).getId();
    }

    private ScannerId getScannerId() {
        return scannerRepository().getScannerMap().values().stream().findFirst().orElseThrow().getId();
    }

    @Test
    @DisplayName("""
        T1. В команде на открытие позиции не указан идентификатор инструмента.
        """)
    void testCase1() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                OpenEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .price(102D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не указан идентификатор инструмента.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T2. В команде на открытие позиции не указан идентификатор сканера.
        """)
    void testCase2() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                OpenEmulatedPosition.builder()
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(102D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не указан идентификатор сканера данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T3. В команде на открытие позиции не указана цена.
        """)
    void testCase3() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                OpenEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не указана цена открытия позиции.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T4. В команде на открытие позиции цена меньше нуля.
        """)
    void testCase4() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                OpenEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(-100D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Цена открытия должна быть больше нуля.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T5. В команде на открытие позиции нулевая цена.
        """)
    void testCase5() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                OpenEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(0D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Цена открытия должна быть больше нуля.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T6. В команде на закрытие позиции не указан идентификатор инструмента.
        """)
    void testCase6() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                CloseEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .price(102D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не указан идентификатор инструмента.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T7. В команде на закрытие позиции не указан идентификатор сканера.
        """)
    void testCase7() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                CloseEmulatedPosition.builder()
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(102D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не указан идентификатор сканера данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T8. В команде на закрытие позиции не указана цена.
        """)
    void testCase8() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                CloseEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не указана цена закрытия позиции.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T9. В команде на закрытие позиции цена меньше нуля.
        """)
    void testCase9() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                CloseEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(-100D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Цена закрытия должна быть больше нуля.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T10. В команде на закрытие позиции нулевая цена.
        """)
    void testCase10() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                CloseEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(0D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Цена закрытия должна быть больше нуля.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T11. В команде на оценку позиций по инструменту не указан идентификатор инструмента.
        """)
    void testCase11() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                EvaluateEmulatedPosition.builder()
                    .price(100D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не указан идентификатор инструмента.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T12. В команде на оценку позиций по инструменту не указана последняя цена инструмента.
        """)
    void testCase12() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                EvaluateEmulatedPosition.builder()
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Не указана последняя цена инструмента.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T13. В команде на оценку позиций по инструменту последняя цена инструмента меньше нуля.
        """)
    void testCase13() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                EvaluateEmulatedPosition.builder()
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(-100D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Последняя цена инструмента должна быть больше нуля.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T14. В команде на оценку позиций по инструменту последняя цена инструмента нулевая.
        """)
    void testCase14() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(
                EvaluateEmulatedPosition.builder()
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(0D)
                    .build()
            )
        );
        assertEquals(1, exception.getConstraintViolations().size());
        assertEquals("Последняя цена инструмента должна быть больше нуля.", getMessage(exception));
    }
}
