package ru.ioque.investfund.scanner.scanning;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.datasource.command.UpdateAggregateHistory;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScanStreamingTest extends BaseScannerTest {

    @Test
    @DisplayName("""
        T1. Поток данных по TGKN и IMOEX. В потоке данных есть сигнал
        """)
    void testCase1() {
        final DatasourceId datasourceId = getDatasourceId();
        initTodayDateTime("2023-12-22T13:00:00");
        initInstrumentDetails(imoexDetails(), tgknDetails());
        initHistoryValues(
            buildTgknHistoryValue("2023-12-19", 99.D, 99.D, 1D, 3000D),
            buildTgknHistoryValue("2023-12-20", 99.D, 99.D, 1D, 1150D),
            buildTgknHistoryValue("2023-12-21", 100.D, 100.D, 1D, 1100D),
            buildImoexHistoryValue("2023-12-10", 2800D, 2900D, 1_000_000D),
            buildImoexHistoryValue("2023-12-20", 2800D, 2900D, 1_500_000D),
            buildImoexHistoryValue("2023-12-21", 2900D, 3000D, 2_000_000D)
        );
        initIntradayValues(
            buildImoexDelta(1L, "10:00:00", 2800D, 1_000_000D),
            buildImoexDelta(2L, "12:00:00", 3100D, 1_200_000D),
            buildTgknBuyDeal(1L, "10:00:00", 100D, 5000D, 1),
            buildTgknBuyDeal(2L, "10:03:00", 100D, 1000D, 1),
            buildTgknBuyDeal(3L, "11:00:00", 100D, 1000D, 1),
            buildTgknBuyDeal(4L, "11:01:00", 100D, 1000D, 1),
            buildTgknBuyDeal(5L, "11:45:00", 102D, 5000D, 1)
        );

        commandBus().execute(new SynchronizeDatasource(datasourceId));
        commandBus().execute(new EnableUpdateInstruments(datasourceId, getTickers(datasourceId)));
        initDefaultScanner(datasourceId);

        runWorkPipeline(datasourceId);

        assertEquals(1, signalJournal().stream().count());
    }

    private void initDefaultScanner(DatasourceId datasourceId) {
        initScanner(datasourceId, TGKN, IMOEX);
    }

    private void initScanner(DatasourceId datasourceId, String... tickers) {
        commandBus().execute(
            CreateScanner.builder()
                .workPeriodInMinutes(1)
                .description("Аномальные объемы, третий эшелон.")
                .datasourceId(datasourceId)
                .tickers(Arrays.stream(tickers).map(Ticker::from).toList())
                .properties(
                    AnomalyVolumeProperties.builder()
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(2)
                        .scaleCoefficient(1.5)
                        .build()
                )
                .build()
        );
    }
}
