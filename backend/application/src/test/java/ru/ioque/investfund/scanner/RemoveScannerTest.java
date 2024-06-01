package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.scanner.command.RemoveScanner;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.position.Position;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.IMOEX;

@DisplayName("REMOVE SCANNER TEST")
public class RemoveScannerTest extends BaseScannerCommandTest {
    @Test
    @DisplayName("""
        T1. Удаление сканера сигналов, по которому есть открытые позиции.
        """)
    void testCase1() {
        initScanner();
        emulatedPositionRepository().save(
            Position.builder()
                .scannerId(getFirstScannerId())
                .openPrice(10D)
                .isOpen(true)
                .instrumentId(getInstruments(getDatasourceId()).get(0).getId()).build()
        );

        var error = assertThrows(
            DomainException.class,
            () -> commandBus().execute(new RemoveScanner(getFirstScannerId()))
        );
        assertEquals("Удаление сканера невозможно, есть эмуляции позиций.", error.getMessage());
        assertEquals(1, emulatedPositionRepository().positions.size());
        assertEquals(1, scannerRepository().scanners.size());
    }

    @Test
    @DisplayName("""
        T2. Удаление сканера сигналов, по которому нет позиций.
        """)
    void testCase2() {
        initScanner();

        commandBus().execute(new RemoveScanner(getFirstScannerId()));

        assertEquals(0, scannerRepository().scanners.size());
    }


    private void initScanner() {
        commandBus()
            .execute(
                createAnomalyVolumeScannerCommandWith()
                    .properties(AnomalyVolumeProperties.builder()
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(3)
                        .scaleCoefficient(1.5)
                        .build())
                    .build()
            );
    }
}
