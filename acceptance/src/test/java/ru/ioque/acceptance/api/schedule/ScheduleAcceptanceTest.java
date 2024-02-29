package ru.ioque.acceptance.api.schedule;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.acceptance.adapters.client.schedule.ScheduleRestClient;
import ru.ioque.acceptance.adapters.client.schedule.request.SaveScheduleRequest;
import ru.ioque.acceptance.adapters.client.schedule.request.SaveScheduleUnitRequest;
import ru.ioque.acceptance.adapters.client.schedule.response.Schedule;
import ru.ioque.acceptance.adapters.client.schedule.response.SystemModuleCode;
import ru.ioque.acceptance.adapters.client.signalscanner.request.PrefSimpleRequest;
import ru.ioque.acceptance.api.BaseApiAcceptanceTest;
import ru.ioque.acceptance.application.tradingdatagenerator.core.HistoryGeneratorConfig;
import ru.ioque.acceptance.application.tradingdatagenerator.core.PercentageGrowths;
import ru.ioque.acceptance.application.tradingdatagenerator.stock.StockTradesGeneratorConfig;
import ru.ioque.acceptance.domain.exchange.Instrument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("МОДУЛЬ \"РАСПИСАНИЕ\"")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ScheduleAcceptanceTest extends BaseApiAcceptanceTest {
    @Autowired
    ScheduleRestClient scheduleRestClient;

    @Test
    @SneakyThrows
    @DisplayName("""
        T1. Биржа зарегистрирована, обновление инструментов включено.
        Создается юнит расписания для работы модуля "Биржа".
        Внутридневные данные проинтегрированы.
        """)
    void testCase1() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = now.toLocalDate().minusMonths(1);
        integrateInstruments(
                instruments().sber().build(),
                instruments().sberp().build()
        );
        enableUpdateInstrumentBy(getInstrumentIds());
        prepareTradingData(startDate, now);
        saveSchedule(
            SaveScheduleRequest
                .builder()
                .saveScheduleUnitRequests(
                    List.of(
                        buildScheduleUnitWith()
                            .objectIds(getInstrumentIds())
                            .systemModuleCode(SystemModuleCode.EXCHANGE)
                            .build()
                    )
                )
                .build()
        );

        long currentMills = System.currentTimeMillis();
        while (getInstrumentById(getInstrumentIds().get(0)).getIntradayValues().isEmpty()) {
            if (System.currentTimeMillis() - currentMills > 60_000) {
                throw new RuntimeException();
            }
        }

        List<Instrument> instruments = getInstrumentIds().stream().map(this::getInstrumentById).toList();
        assertEquals(2, instruments.stream().filter(row -> !row.getDailyValues().isEmpty()).toList().size());
        assertEquals(2, instruments.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T2. Биржа зарегистрирована, внутридневные данные проинтегрированы, создан сканер сигналов по аномальным объемам.
        Создается юнит расписания для работы модуля "Сканер сигнало".
        Сгенерирован сигнал к покупке.
        """)
    void testCase2() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = now.toLocalDate().minusMonths(1);
        datasetManager().initInstruments(
            List.of(
                instruments().sber().build(),
                instruments().sberp().build()
            )
        );
        prepareTradingData(startDate, now);
        fullIntegrate();
        prepareSignalScanner();
        saveSchedule(
            SaveScheduleRequest
                .builder()
                .saveScheduleUnitRequests(
                    List.of(
                        buildScheduleUnitWith()
                            .objectIds(getInstrumentIds())
                            .systemModuleCode(SystemModuleCode.SIGNAL_SCANNER)
                            .build()
                    )
                )
                .build()
        );

        long currentMills = System.currentTimeMillis();
        while (getSignals().isEmpty()) {
            if (System.currentTimeMillis() - currentMills > 60_000) {
                throw new RuntimeException();
            }
        }
        assertEquals(1, getSignals().size());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T3. Биржа зарегистрирована.
        Создается юнит расписания для работы модуля "Биржа".
        Создается юнит расписания для работы модуля "Сканер сигналов".
        Проинтегрированы внутридневные данные.
        Сгенерирован сигнал к покупке.
        Порядок работы модулей соблюден.
        """)
    void testCase3() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = now.toLocalDate().minusMonths(1);
        integrateInstruments(
            instruments().sber().build(),
            instruments().sberp().build()
        );
        enableUpdateInstrumentBy(getInstrumentIds());
        prepareTradingData(startDate, now);
        prepareSignalScanner();

        saveSchedule(
            SaveScheduleRequest
                .builder()
                .saveScheduleUnitRequests(
                    List.of(
                        buildScheduleUnitWith()
                            .objectIds(getInstrumentIds())
                            .systemModuleCode(SystemModuleCode.EXCHANGE)
                            .priority(1)
                            .build(),
                        buildScheduleUnitWith()
                            .objectIds(getInstrumentIds())
                            .systemModuleCode(SystemModuleCode.SIGNAL_SCANNER)
                            .priority(2)
                            .build()
                    )
                )
                .build()
        );

        long currentMills = System.currentTimeMillis();
        while (getSignals().isEmpty()) {
            if (System.currentTimeMillis() - currentMills > 60_000) {
                throw new RuntimeException();
            }
        }
        List<Instrument> instruments = getInstrumentIds().stream().map(this::getInstrumentById).toList();
        assertEquals(2, instruments.stream().filter(row -> !row.getDailyValues().isEmpty()).toList().size());
        assertEquals(2, instruments.stream().filter(row -> !row.getIntradayValues().isEmpty()).toList().size());
        assertEquals(1, getSignals().size());
    }

    private void prepareSignalScanner() {
        addSignalScanner(
            PrefSimpleRequest.builder()
                .spreadParam(1D)
                .description("desc")
                .ids(getInstrumentIds())
                .build()
        );
    }

    private void prepareTradingData(LocalDate startDate, LocalDateTime now) {
        datasetManager().initDailyResultValue(
            Stream.concat(
                    generator()
                        .generateStockHistory(
                            HistoryGeneratorConfig
                                .builder()
                                .ticker("SBER")
                                .startClose(245.)
                                .startOpen(242.)
                                .startValue(1_000_000D)
                                .days(30)
                                .startDate(startDate)
                                .openPricePercentageGrowths(List.of(new PercentageGrowths(50D, 1D)))
                                .closePricePercentageGrowths(List.of(new PercentageGrowths(50D, 1D)))
                                .valuePercentageGrowths(List.of(new PercentageGrowths(50D, 1D)))
                                .build()
                        )
                        .stream(),
                    generator()
                        .generateStockHistory(
                            HistoryGeneratorConfig
                                .builder()
                                .ticker("SBERP")
                                .startClose(242.)
                                .startOpen(239.)
                                .startValue(1_000_000D)
                                .days(30)
                                .startDate(startDate)
                                .openPricePercentageGrowths(List.of(new PercentageGrowths(25D, 1D)))
                                .closePricePercentageGrowths(List.of(new PercentageGrowths(25D, 1D)))
                                .valuePercentageGrowths(List.of(new PercentageGrowths(25D, 1D)))
                                .build()
                        )
                        .stream()
                )
                .toList()
        );
        datasetManager().initIntradayValue(
            Stream
                .concat(
                    generator().generateStockTrades(
                        StockTradesGeneratorConfig
                            .builder()
                            .ticker("SBER")
                            .numTrades(2000)
                            .startPrice(245.)
                            .startValue(200_000D)
                            .date(now.toLocalDate())
                            .startTime(LocalTime.parse("10:00"))
                            .pricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                            .valuePercentageGrowths(List.of(new PercentageGrowths(2D, 1D)))
                            .build()
                    ).stream(),
                    generator().generateStockTrades(
                        StockTradesGeneratorConfig
                            .builder()
                            .ticker("SBERP")
                            .numTrades(2000)
                            .startPrice(242.)
                            .startValue(200_000D)
                            .date(now.toLocalDate())
                            .startTime(LocalTime.parse("10:00"))
                            .pricePercentageGrowths(List.of(new PercentageGrowths(-15D, 1D)))
                            .valuePercentageGrowths(List.of(new PercentageGrowths(-2D, 1D)))
                            .build()
                    ).stream()
                )
                .toList()
        );
    }

    private static SaveScheduleUnitRequest.SaveScheduleUnitRequestBuilder buildScheduleUnitWith() {
        return SaveScheduleUnitRequest.builder()
            .objectIds(List.of())
            .startTime(LocalTime.parse("00:00"))
            .stopTime(LocalTime.parse("23:59"))
            .priority(1);
    }

    private Schedule saveSchedule(SaveScheduleRequest scheduleRequest) {
        scheduleRestClient.saveSchedule(scheduleRequest);
        return scheduleRestClient.getSchedule();
    }
}
