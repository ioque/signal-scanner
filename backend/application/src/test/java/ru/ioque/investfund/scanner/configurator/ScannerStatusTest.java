package ru.ioque.investfund.scanner.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.scanner.command.ActivateScanner;
import ru.ioque.investfund.application.modules.scanner.command.DeactivateScanner;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.entity.ScannerStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

public class ScannerStatusTest extends BaseConfiguratorTest {
    @Test
    @DisplayName("""
        T1. Деактивация сканера сигнала и повторная активация.
        """)
    void testCase1() {
        prepareTestCase();

        commandBus().execute(new DeactivateScanner(getFirstScannerId()));
        runWorkPipeline(getDatasourceId());

        assertEquals(1, scannerRepository().scanners.size());
        assertEquals(0, getScanner(getFirstScannerId()).getSignals().size());
        assertEquals(ScannerStatus.INACTIVE, getScanner(getFirstScannerId()).getStatus());

        commandBus().execute(new ActivateScanner(getFirstScannerId()));
        runWorkPipeline(getDatasourceId());

        assertEquals(2, signalJournal().stream().count());
        assertEquals(ScannerStatus.ACTIVE, getScanner(getFirstScannerId()).getStatus());
    }


    private void prepareTestCase() {
        initTodayDateTime("2023-12-22T13:00:00");
        initHistoryValues(
            historyFixture.tgkbHistoryValue("2023-12-19", 99.D, 99.D, 1D, 2000D),
            historyFixture.tgkbHistoryValue("2023-12-20", 99.D, 99.D, 1D, 1000D),
            historyFixture.tgkbHistoryValue("2023-12-21", 100.D, 100.D, 1D, 1500D),
            historyFixture.tgknHistoryValue("2023-12-19", 99.D, 99.D, 1D, 3000D),
            historyFixture.tgknHistoryValue("2023-12-20", 99.D, 99.D, 1D, 1150D),
            historyFixture.tgknHistoryValue("2023-12-21", 100.D, 100.D, 1D, 1100D),
            historyFixture.imoexHistoryValue("2023-12-10", 2800D, 2900D, 1_000_000D),
            historyFixture.imoexHistoryValue("2023-12-20", 2800D, 2900D, 1_500_000D),
            historyFixture.imoexHistoryValue("2023-12-21", 2900D, 3000D, 2_000_000D)
        );
        initIntradayValues(
            intradayFixture.imoexDelta(1L, "10:00:00", 3100D, 1_000_000D),
            intradayFixture.imoexDelta(2L, "12:00:00", 3400D, 1_200_000D),
            //TGKB
            intradayFixture.tgkbBuyDeal(1L, "10:00:00", 100D, 6000D, 1),
            intradayFixture.tgkbBuyDeal(2L, "10:16:00", 100D, 1000D, 1),
            intradayFixture.tgkbBuyDeal(3L, "11:00:00", 100D, 1000D, 1),
            intradayFixture.tgkbBuyDeal(4L, "11:10:00", 100D, 1000D, 1),
            intradayFixture.tgkbBuyDeal(5L, "11:50:00", 102D, 6000D, 1),
            //TGKN
            intradayFixture.tgknBuyDeal(1L, "10:00:00", 100D, 5000D, 1),
            intradayFixture.tgknBuyDeal(2L, "10:03:00", 100D, 1000D, 1),
            intradayFixture.tgknBuyDeal(3L, "11:00:00", 100D, 1000D, 1),
            intradayFixture.tgknBuyDeal(4L, "11:01:00", 100D, 1000D, 1),
            intradayFixture.tgknBuyDeal(5L, "11:45:00", 102D, 5000D, 1)
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
        assertEquals(ScannerStatus.ACTIVE, getScanner(getFirstScannerId()).getStatus());
    }
}
