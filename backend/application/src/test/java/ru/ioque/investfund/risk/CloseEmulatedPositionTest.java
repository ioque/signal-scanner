package ru.ioque.investfund.risk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.application.modules.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

public class CloseEmulatedPositionTest extends RiskManagerTest {
    @Test
    @DisplayName("""
        T1. Закрытие позиции по инструменту TGKN.
        """)
    void testCase1() {
        commandBus().execute(
            OpenEmulatedPosition.builder()
                .scannerId(getScannerId())
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(102D)
                .build()
        );

        commandBus().execute(
            CloseEmulatedPosition.builder()
                .scannerId(getScannerId())
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(122D)
                .build()
        );
        final Optional<EmulatedPosition> actualPosition = emulatedPositionRepository().findActualBy(getInstrumentIdBy(TGKN), getScannerId());
        assertTrue(actualPosition.isPresent());
        assertNotNull(actualPosition.get().getId());
        assertFalse(actualPosition.get().getIsOpen());
        assertEquals(122D, actualPosition.get().getClosePrice());
        assertEquals(102D, actualPosition.get().getOpenPrice());
        assertEquals(122D, actualPosition.get().getLastPrice());
        assertEquals(20, Math.round(actualPosition.get().getProfit()));
        assertEquals(getInstrumentIdBy(TGKN), actualPosition.get().getInstrument().getId());
        assertEquals(getScannerId(), actualPosition.get().getScanner().getId());
    }

    @Test
    @DisplayName("""
        T2. Закрытие несуществующей позиции (инструмент).
        """)
    void testCase2() {
        final InstrumentId instrumentId = InstrumentId.from(UUID.randomUUID());
        final EntityNotFoundException error = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(
                CloseEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .instrumentId(instrumentId)
                    .price(102D)
                    .build()
            )
        );
        assertEquals(
            String.format(
                "Позиции по инструменту[id=%s] и сканеру[id=%s] не существует.",
                instrumentId,
                getScannerId()
            ),
            error.getMessage()
        );
    }

    @Test
    @DisplayName("""
        T3. Закрытие несуществующей позиции (сканер).
        """)
    void testCase3() {
        final ScannerId scannerId = ScannerId.from(UUID.randomUUID());
        final EntityNotFoundException error = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(
                CloseEmulatedPosition.builder()
                    .scannerId(scannerId)
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(102D)
                    .build()
            )
        );
        assertEquals(
            String.format(
                "Позиции по инструменту[id=%s] и сканеру[id=%s] не существует.",
                getInstrumentIdBy(TGKN),
                scannerId
            ),
            error.getMessage()
        );
    }
}
