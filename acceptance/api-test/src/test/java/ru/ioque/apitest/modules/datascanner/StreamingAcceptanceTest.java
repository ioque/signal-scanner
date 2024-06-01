package ru.ioque.apitest.modules.datascanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.apitest.DatasourceEmulatedTest;
import ru.ioque.apitest.dataset.DatasetRepository;
import ru.ioque.core.datagenerator.config.DealsGeneratorConfig;
import ru.ioque.core.datagenerator.config.DeltasGeneratorConfig;
import ru.ioque.core.datagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.datagenerator.core.PercentageGrowths;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dataset.DefaultInstrumentSet;
import ru.ioque.core.dataset.StreamingStorage;
import ru.ioque.core.dataset.WorkMode;
import ru.ioque.core.dto.datasource.request.DatasourceRequest;
import ru.ioque.core.dto.scanner.request.AnomalyVolumePropertiesDto;
import ru.ioque.core.dto.scanner.request.CreateScannerRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class StreamingAcceptanceTest extends DatasourceEmulatedTest {
    @Autowired
    DatasetRepository datasetRepository;


    @BeforeEach
    void initDateTime() {
        initDateTime(getDateTimeNow());
        datasourceClient().createDatasource(
                DatasourceRequest.builder()
                        .name("Московская Биржа")
                        .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
                        .url(datasourceHost)
                        .build()
        );
    }

    private static LocalDateTime getDateTimeNow() {
        return LocalDateTime.parse("2024-03-04T11:00:00");
    }

    @Test
    void testCase1() throws InterruptedException {
        final UUID datasourceId = getAllDatasource().get(0).getId();
        final LocalDateTime now = getDateTimeNow();
        final LocalDate startDate = now.toLocalDate().minusMonths(1);
        initDataset(
                Dataset.builder()
                        .workMode(WorkMode.STREAMING)
                        .instruments(List.of(DefaultInstrumentSet.imoex(), DefaultInstrumentSet.tgkn()))
                        .historyValues(
                                Stream.concat(
                                                generator()
                                                        .generateHistory(
                                                                HistoryGeneratorConfig
                                                                        .builder()
                                                                        .ticker("TGKN")
                                                                        .startClose(10.)
                                                                        .startOpen(10.)
                                                                        .startValue(1_000_000D)
                                                                        .days(30)
                                                                        .startDate(startDate)
                                                                        .openPricePercentageGrowths(List.of(
                                                                                new PercentageGrowths(50D, 0.5),
                                                                                new PercentageGrowths(-50D, 0.5)
                                                                        ))
                                                                        .closePricePercentageGrowths(List.of(
                                                                                new PercentageGrowths(50D, 0.5),
                                                                                new PercentageGrowths(-50D, 0.5)
                                                                        ))
                                                                        .valuePercentageGrowths(List.of(
                                                                                new PercentageGrowths(50D, 0.5),
                                                                                new PercentageGrowths(-50D, 0.5)
                                                                        ))
                                                                        .build()
                                                        )
                                                        .stream(),
                                                generator()
                                                        .generateHistory(
                                                                HistoryGeneratorConfig
                                                                        .builder()
                                                                        .ticker("IMOEX")
                                                                        .startClose(10.)
                                                                        .startOpen(10.)
                                                                        .startValue(2_000_000D)
                                                                        .days(30)
                                                                        .startDate(startDate)
                                                                        .openPricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                                                                        .closePricePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                                                                        .valuePercentageGrowths(List.of(new PercentageGrowths(20D, 1D)))
                                                                        .build()
                                                        )
                                                        .stream()
                                        )
                                        .toList()
                        )
                        .intradayValues(
                                Stream
                                        .concat(
                                                generator().generateDeltas(
                                                        DeltasGeneratorConfig
                                                                .builder()
                                                                .ticker("IMOEX")
                                                                .numTrades(2000)
                                                                .startPrice(10.)
                                                                .startValue(200_000D)
                                                                .date(now.toLocalDate())
                                                                .startTime(LocalTime.parse("10:00"))
                                                                .pricePercentageGrowths(List.of(new PercentageGrowths(80D, 1D)))
                                                                .valuePercentageGrowths(List.of(new PercentageGrowths(200D, 1D)))
                                                                .build()
                                                ).stream(),
                                                generator().generateDeals(
                                                        DealsGeneratorConfig
                                                                .builder()
                                                                .ticker("TGKN")
                                                                .numTrades(2000)
                                                                .startPrice(10.)
                                                                .startValue(200_000D)
                                                                .date(now.toLocalDate())
                                                                .startTime(LocalTime.parse("10:00"))
                                                                .pricePercentageGrowths(List.of(new PercentageGrowths(80D, 1D)))
                                                                .valuePercentageGrowths(List.of(new PercentageGrowths(200D, 1D)))
                                                                .build()
                                                ).stream()
                                        )
                                        .toList()
                        )
                        .build()
        );

        synchronizeDatasource(datasourceId);
        enableUpdateInstrumentBy(datasourceId, getTickers(datasourceId));
        updateAggregatedTotals(datasourceId);
        createScanner(
                CreateScannerRequest.builder()
                        .workPeriodInMinutes(1)
                        .datasourceId(datasourceId)
                        .description("desc")
                        .tickers(getTickers(datasourceId))
                        .properties(
                                AnomalyVolumePropertiesDto.builder()
                                        .historyPeriod(20)
                                        .scaleCoefficient(1.5)
                                        .indexTicker("IMOEX")
                                        .build()
                        )
                        .build()
        );

        serviceClient().initializePipeline();

        final StreamingStorage streamingStorage = (StreamingStorage) datasetRepository.datasetStorage;
        streamingStorage.getStreamingThread().start();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> publishIntradayData(datasourceId), 0, 10, TimeUnit.SECONDS);
        service.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }
}
