package ru.ioque.investfund.scanner.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.integration.event.SignalRegistered;
import ru.ioque.investfund.application.modules.datasource.command.UpdateAggregateHistory;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.application.modules.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.application.modules.scanner.command.ProduceSignal;
import ru.ioque.investfund.application.modules.scanner.command.RemoveScanner;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemoveScannerTest extends BaseConfiguratorTest {
    @Test
    @DisplayName("""
        T1. Удаление сканера сигналов, по которому есть открытые позиции.
        """)
    void testCase1() {
        prepareTestCase1();

        var error = assertThrows(
            DomainException.class,
            () -> commandBus().execute(new RemoveScanner(getFirstScannerId()))
        );
        assertEquals("Удаление сканера невозможно, есть эмуляции позиций.", error.getMessage());
        assertEquals(2, emulatedPositionRepository().emulatedPositions.size());
        assertEquals(1, scannerRepository().scanners.size());
    }

    @Test
    @DisplayName("""
        T2. Удаление сканера сигналов, по которому нет открытых позиций.
        """)
    void testCase2() {
        prepareTestCase2();

        commandBus().execute(new RemoveScanner(getFirstScannerId()));

        assertEquals(0, scannerRepository().scanners.size());
    }

    private void prepareTestCase1() {
        initTodayDateTime("2023-12-22T13:00:00");
        initHistoryValues(
            buildTgkbHistoryValue("2023-12-19", 99.D, 99.D, 1D, 2000D),
            buildTgkbHistoryValue("2023-12-20", 99.D, 99.D, 1D, 1000D),
            buildTgkbHistoryValue("2023-12-21", 100.D, 100.D, 1D, 1500D),
            buildTgknHistoryValue("2023-12-19", 99.D, 99.D, 1D, 3000D),
            buildTgknHistoryValue("2023-12-20", 99.D, 99.D, 1D, 1150D),
            buildTgknHistoryValue("2023-12-21", 100.D, 100.D, 1D, 1100D),
            buildImoexHistoryValue("2023-12-10", 2800D, 2900D, 1_000_000D),
            buildImoexHistoryValue("2023-12-20", 2800D, 2900D, 1_500_000D),
            buildImoexHistoryValue("2023-12-21", 2900D, 3000D, 2_000_000D)
        );
        initIntradayValues(
            buildImoexDelta(1L, "10:00:00", 3100D, 1_000_000D),
            buildImoexDelta(2L, "12:00:00", 3400D, 1_200_000D),
            //TGKB
            buildTgkbBuyDeal(1L, "10:00:00", 100D, 6000D, 1),
            buildTgkbBuyDeal(2L, "10:16:00", 100D, 1000D, 1),
            buildTgkbBuyDeal(3L, "11:00:00", 100D, 1000D, 1),
            buildTgkbBuyDeal(4L, "11:10:00", 100D, 1000D, 1),
            buildTgkbBuyDeal(5L, "11:50:00", 102D, 6000D, 1),
            //TGKN
            buildTgknBuyDeal(1L, "10:00:00", 100D, 5000D, 1),
            buildTgknBuyDeal(2L, "10:03:00", 100D, 1000D, 1),
            buildTgknBuyDeal(3L, "11:00:00", 100D, 1000D, 1),
            buildTgknBuyDeal(4L, "11:01:00", 100D, 1000D, 1),
            buildTgknBuyDeal(5L, "11:45:00", 102D, 5000D, 1)
        );
        commandBus().execute(new UpdateAggregateHistory(getDatasourceId()));
        commandBus()
            .execute(
                buildCreateAnomalyVolumeScannerWith()
                    .properties(AnomalyVolumeProperties.builder()
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(3)
                        .scaleCoefficient(1.5)
                        .build())
                    .build()
            );
        commandBus().execute(new PublishIntradayData(getDatasourceId()));
        commandBus().execute(new ProduceSignal(getDatasourceId(), dateTimeProvider().nowDateTime()));
        eventPublisher()
            .getEvents()
            .stream()
            .filter(event -> event.getClass().equals(SignalRegistered.class))
            .map(SignalRegistered.class::cast)
            .forEach(event -> commandBus()
                .execute(
                    OpenEmulatedPosition.builder()
                        .price(event.getPrice())
                        .scannerId(ScannerId.from(event.getScannerId()))
                        .instrumentId(InstrumentId.from(event.getInstrumentId()))
                        .build()
                )
            );
        assertEquals(2, getScanner(getFirstScannerId()).getSignals().size());
        assertEquals(2, emulatedPositionRepository().emulatedPositions.size());
    }

    private void prepareTestCase2() {
        commandBus().execute(buildCreateAnomalyVolumeScannerWith().build());
        assertEquals(1, scannerRepository().scanners.size());
    }
}
