package ru.ioque.investfund.risk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.application.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPosition;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EvaluateEmulatedPositionTest extends RiskManagerTest {
    @Test
    @DisplayName("""
        T1. Оцена позиции.
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
            EvaluateEmulatedPosition.builder()
                .track(UUID.randomUUID())
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(122D)
                .build()
        );

        final List<EmulatedPosition> positions = emulatedPositionRepository().findAllBy(getInstrumentIdBy(TGKN));
        assertEquals(1, positions.size());
        assertNotNull(positions.get(0).getId());
        assertTrue(positions.get(0).getIsOpen());
        assertNull(positions.get(0).getClosePrice());
        assertEquals(102D, positions.get(0).getOpenPrice());
        assertEquals(122D, positions.get(0).getLastPrice());
        assertEquals(20, Math.round(positions.get(0).getProfit()));
        assertEquals(getInstrumentIdBy(TGKN), positions.get(0).getInstrumentId());
        assertEquals(getScannerId(), positions.get(0).getScannerId());
    }
}
