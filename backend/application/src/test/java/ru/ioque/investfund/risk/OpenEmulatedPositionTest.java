package ru.ioque.investfund.risk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenEmulatedPositionTest extends RiskManagerTest {
    @Test
    @DisplayName("""
        T1. Открытие позиции по инструменту TGKN.
        """)
    void testCase1() {
        commandBus().execute(
            OpenEmulatedPosition.builder()
                .scannerId(getScannerId())
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(102D)
                .build()
        );

        final List<EmulatedPosition> positions = emulatedPositionRepository().findAllBy(getInstrumentIdBy(TGKN));
        assertEquals(1, positions.size());
        assertNotNull(positions.get(0).getId());
        assertTrue(positions.get(0).getIsOpen());
        assertNull(positions.get(0).getClosePrice());
        assertEquals(102D, positions.get(0).getOpenPrice());
        assertEquals(102D, positions.get(0).getLastPrice());
        assertEquals(0, positions.get(0).getProfit());
        assertEquals(getInstrumentIdBy(TGKN), positions.get(0).getInstrument().getId());
        assertEquals(getScannerId(), positions.get(0).getScanner().getId());
    }

    @Test
    @DisplayName("""
        T2. Открытие позиции по несуществующему инструменту.
        """)
    void testCase2() {
        final InstrumentId instrumentId = InstrumentId.from(UUID.randomUUID());
        final EntityNotFoundException error = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(
                OpenEmulatedPosition.builder()
                    .scannerId(getScannerId())
                    .instrumentId(instrumentId)
                    .price(102D)
                    .build()
            )
        );
        assertEquals(String.format("Инструмент[id=%s] не существует.", instrumentId), error.getMessage());
    }

    @Test
    @DisplayName("""
        T3. Открытие позиции по несуществующему сканеру данных.
        """)
    void testCase3() {
        final ScannerId scannerId = ScannerId.from(UUID.randomUUID());
        final EntityNotFoundException error = assertThrows(
            EntityNotFoundException.class,
            () -> commandBus().execute(
                OpenEmulatedPosition.builder()
                    .scannerId(scannerId)
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .price(102D)
                    .build()
            )
        );
        assertEquals(String.format("Сканер[id=%s] не существует.", scannerId), error.getMessage());
    }

    @Test
    @DisplayName("""
        T4. Повторное открытие позиции по инструменту TGKN.
        """)
    void testCase4() {
        commandBus().execute(
            OpenEmulatedPosition.builder()
                .scannerId(getScannerId())
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(102D)
                .build()
        );

        commandBus().execute(
            OpenEmulatedPosition.builder()
                .scannerId(getScannerId())
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(102D)
                .build()
        );

        final List<EmulatedPosition> positions = emulatedPositionRepository().findAllBy(getInstrumentIdBy(TGKN));
        assertEquals(1, positions.size());
        assertNotNull(positions.get(0).getId());
        assertTrue(positions.get(0).getIsOpen());
        assertNull(positions.get(0).getClosePrice());
        assertEquals(102D, positions.get(0).getOpenPrice());
        assertEquals(102D, positions.get(0).getLastPrice());
        assertEquals(0, positions.get(0).getProfit());
        assertEquals(getInstrumentIdBy(TGKN), positions.get(0).getInstrument().getId());
        assertEquals(getScannerId(), positions.get(0).getScanner().getId());
    }
}
