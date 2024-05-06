package ru.ioque.investfund.scanner.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.application.modules.scanner.command.ActivateScanner;
import ru.ioque.investfund.application.modules.scanner.command.DeactivateScanner;
import ru.ioque.investfund.application.modules.scanner.command.ProduceSignal;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerStatusTest extends BaseConfiguratorTest {
    @Test
    @DisplayName("""
        T1. Декативация сканера сигнала и повторная активация.
        """)
    void testCase1() {
        prepareTestCase();

        commandBus().execute(new DeactivateScanner(getFirstScannerId()));
        commandBus().execute(new ProduceSignal(getDatasourceId(), dateTimeProvider().nowDateTime()));

        assertEquals(1, scannerRepository().scanners.size());
        assertEquals(0, getScanner(getFirstScannerId()).getSignals().size());
        assertEquals(ScannerStatus.INACTIVE, getScanner(getFirstScannerId()).getStatus());

        commandBus().execute(new ActivateScanner(getFirstScannerId()));
        commandBus().execute(new ProduceSignal(getDatasourceId(), dateTimeProvider().nowDateTime()));

        assertEquals(2, getScanner(getFirstScannerId()).getSignals().size());
        assertEquals(ScannerStatus.ACTIVE, getScanner(getFirstScannerId()).getStatus());
    }


    private void prepareTestCase() {
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
        commandBus().execute(new IntegrateTradingDataCommand(getDatasourceId()));
        assertEquals(ScannerStatus.ACTIVE, getScanner(getFirstScannerId()).getStatus());
    }
}
