package ru.ioque.investfund.pipeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.EnableUpdateInstruments;
import ru.ioque.investfund.application.modules.datasource.command.PublishAggregatedHistory;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.application.modules.scanner.command.CreateScanner;
import ru.ioque.investfund.application.modules.telegrambot.command.Subscribe;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.IMOEX;

@DisplayName("PIPELINE CONFIGURATOR TEST")
public class PipelineManagerTest extends BaseTest {

    @BeforeEach
    void beforeEach() {
        initInstrumentDetails(
            instrumentFixture.imoexDetails(),
            instrumentFixture.tgkbDetails(),
            instrumentFixture.tgknDetails()
        );
        initAggregatedTotals(
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
        initIntradayData(
            intradayFixture.imoexDelta(1L, "10:00:00", 2800D, 1_000_000D),
            intradayFixture.imoexDelta(2L, "12:00:00", 3100D, 1_200_000D),
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
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        commandBus().execute(new SynchronizeDatasource(getDatasourceId()));
        commandBus().execute(new EnableUpdateInstruments(getDatasourceId(), getTickers(getDatasourceId())));
        commandBus().execute(new PublishAggregatedHistory(getDatasourceId()));
        commandBus().execute(
            CreateScanner.builder()
                .workPeriodInMinutes(1)
                .description("Аномальные объемы, третий эшелон.")
                .datasourceId(getDatasourceId())
                .tickers(getTickers(getDatasourceId()))
                .properties(
                    AnomalyVolumeProperties.builder()
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(2)
                        .scaleCoefficient(1.5)
                        .build()
                )
                .build()
        );
        commandBus().execute(new Subscribe(1L, "kukusuku"));
        pipelineManager().start();
        loggerProvider().clearLogs();
    }


    @Test
    @DisplayName("""
        T1. Построение топологии пайплайна
        """)
    void testCase1() {
        buildDefaultTopology();

        commandBus().execute(new PublishIntradayData(getDatasourceId()));

        assertEquals(2, signalJournal().getAll().count());
        //assertEquals(2, emulatedPositionRepository().positions.size());
        //assertEquals(3, telegramMessageSender().messages.get(1L).size());
    }

}
