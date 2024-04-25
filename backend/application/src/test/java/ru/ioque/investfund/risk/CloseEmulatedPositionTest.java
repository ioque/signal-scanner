package ru.ioque.investfund.risk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.risk.command.CloseEmulatedPosition;
import ru.ioque.investfund.application.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CloseEmulatedPositionTest extends RiskManagerTest {
    @Test
    @DisplayName("""
        T1. Закрытие позиции по инструменту TGKN.
        """)
    void testCase1() {
        commandBus().execute(
            OpenEmulatedPosition.builder()
                .track(UUID.randomUUID())
                .scannerId(getScannerId())
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(102D)
                .build()
        );

        commandBus().execute(
            CloseEmulatedPosition.builder()
                .track(UUID.randomUUID())
                .scannerId(getScannerId())
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(122D)
                .build()
        );
        final List<EmulatedPosition> positions = emulatedPositionRepository().findAllBy(getInstrumentIdBy(TGKN));
        assertEquals(1, positions.size());
        assertNotNull(positions.get(0).getId());
        assertFalse(positions.get(0).getIsOpen());
        assertEquals(122D, positions.get(0).getClosePrice());
        assertEquals(102D, positions.get(0).getOpenPrice());
        assertEquals(122D, positions.get(0).getLastPrice());
        assertEquals(20, Math.round(positions.get(0).getProfit()));
        assertEquals(getInstrumentIdBy(TGKN), positions.get(0).getInstrumentId());
        assertEquals(getScannerId(), positions.get(0).getScannerId());
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
                    .track(UUID.randomUUID())
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
                    .track(UUID.randomUUID())
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
