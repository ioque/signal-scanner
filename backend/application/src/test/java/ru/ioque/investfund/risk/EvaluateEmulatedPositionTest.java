package ru.ioque.investfund.risk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.application.modules.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPosition;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

public class EvaluateEmulatedPositionTest extends RiskManagerTest {
    @Test
    @DisplayName("""
        T1. Оцена позиции.
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
            EvaluateEmulatedPosition.builder()
                .instrumentId(getInstrumentIdBy(TGKN))
                .price(122D)
                .build()
        );

        final Optional<EmulatedPosition> actualPosition = emulatedPositionRepository().findActualBy(getInstrumentIdBy(TGKN), getScannerId());
        assertTrue(actualPosition.isPresent());
        assertNotNull(actualPosition.get().getId());
        assertTrue(actualPosition.get().getIsOpen());
        assertNull(actualPosition.get().getClosePrice());
        assertEquals(102D, actualPosition.get().getOpenPrice());
        assertEquals(122D, actualPosition.get().getLastPrice());
        assertEquals(20, Math.round(actualPosition.get().getProfit()));
        assertEquals(getInstrumentIdBy(TGKN), actualPosition.get().getInstrument().getId());
        assertEquals(getScannerId(), actualPosition.get().getScanner().getId());
    }
}
