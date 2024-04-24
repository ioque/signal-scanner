package ru.ioque.investfund.scanner.scanning;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.application.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.application.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.application.scanner.event.SignalRegistered;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.application.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.application.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProduceSignalCommandTest extends BaseScannerTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        initInstrumentDetails(imoex(), tgkbDetails(), tgknDetails());
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        commandBus().execute(
            CreateScannerCommand.builder()
                .datasourceId(getDatasourceId())
                .workPeriodInMinutes(1)
                .tickers(List.of(new Ticker(TGKN), new Ticker(TGKB), new Ticker(IMOEX)))
                .description("description")
                .properties(
                    AnomalyVolumeProperties.builder()
                        .scaleCoefficient(1.5)
                        .indexTicker(new Ticker(IMOEX))
                        .historyPeriod(3)
                        .build()
                )
                .build()
        );
        loggerProvider().clearLogs();
    }

    @Test
    @DisplayName("""
        T1. В команду запуска сканирования не передан идентификатор источника данных.
        """)
    void testCase1() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new ProduceSignalCommand(null, getToday()))
        );
        assertEquals("Не передан идентификатор источника данных.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T2. В команду запуска сканирования не передан watermark.
        """)
    void testCase2() {
        final ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class,
            () -> commandBus().execute(new ProduceSignalCommand(getDatasourceId(), null))
        );
        assertEquals("Не передан watermark.", getMessage(exception));
    }

    @Test
    @DisplayName("""
        T3. Зарегистрирован сигнал к покупке по тикеру - TGKN, алгоритм - аномальные объемы.
        Сгенерировано события - зарегистрирован сигнал по тикеру TGKN.
        """)
    void testCase3() {
        prepareTestCase3();

        commandBus().execute(new ProduceSignalCommand(getDatasourceId(), dateTimeProvider().nowDateTime()));

        final Optional<SignalRegistered> signalFoundEvent = findSignalFoundEvent();
        assertTrue(signalFoundEvent.isPresent());
        assertNotNull(signalFoundEvent.get());
        assertEquals(getScannerId().getUuid(), signalFoundEvent.get().getScannerId());
        assertEquals(getTgknSnapshot().getInstrumentId().getUuid(), signalFoundEvent.get().getInstrumentId());
        assertTrue(signalFoundEvent.get().getIsBuy());
        assertEquals(dateTimeProvider().nowDateTime(), signalFoundEvent.get().getCreatedAt());
    }

    @Test
    @DisplayName("""
        T4. Ранее были зарегистрированы сигналы к покупке по двум тикерам - TGKN, TGKB, алгоритм - аномальные объемы.
        По тикеру TGKN найден повторный сигнал к покупке, по тикеру TGKB найден сигнал к продаже.
        Сигнал к покупке TGKN не зарегистрирован, сигнал к покупке TGKB закрыт, сигнал к продаже TGKB зарегистрирован.
        """)
    void testCase4() {
        prepareTestCase4();

        commandBus().execute(new ProduceSignalCommand(getDatasourceId(), dateTimeProvider().nowDateTime()));

        SignalScanner scanner = scannerRepository().findAllBy(getDatasourceId()).stream().findFirst().orElseThrow();
        assertEquals(3, scanner.getSignals().size());
        assertEquals(2, scanner.getSignals().stream().filter(Signal::isOpen).count());
    }

    @Test
    @DisplayName("""
        T5. В данных найден сигнал к продаже TGKN, сигнала к покупке по инструменту не было. Сигнал не зарегистрирован.
        """)
    void testCase5() {
        prepareTestCase5();

        commandBus().execute(new ProduceSignalCommand(getDatasourceId(), dateTimeProvider().nowDateTime()));

        SignalScanner scanner = scannerRepository().findAllBy(getDatasourceId()).stream().findFirst().orElseThrow();
        assertEquals(0, scanner.getSignals().size());
    }

    @Test
    @DisplayName("""
        T6. Ранее был зарегистрирован сигнал к продаже TGKN. Найден сигнал к покупке TGKN.
        Сигнал к продаже TGKN закрыт, сигнал к покупке TGKN зарегистрирован.
        """)
    void testCase6() {
        prepareTestCase6();

        commandBus().execute(new ProduceSignalCommand(getDatasourceId(), dateTimeProvider().nowDateTime()));

        SignalScanner scanner = scannerRepository().findAllBy(getDatasourceId()).stream().findFirst().orElseThrow();
        assertEquals(2, scanner.getSignals().size());
        assertEquals(1, scanner.getSignals().stream().filter(Signal::isOpen).count());
        assertEquals(1, scanner.getSignals().stream().filter(Signal::isBuy).count());
        assertEquals(1, scanner.getSignals().stream().filter(Signal::isSell).count());
    }

    private ScannerId getScannerId() {
        return scannerRepository().getScannerMap().values().stream().findFirst().map(SignalScanner::getId).orElseThrow();
    }

    private void prepareTestCase3() {
        final SignalScanner scanner = scannerRepository().findAllBy(getDatasourceId()).stream().findFirst().orElseThrow();
        initTodayDateTime("2023-12-25T12:00:00");
        initHistoryValues(
            buildTgknHistoryValue("2023-12-22", 99.D, 99.D, 99D, 1000D),
            buildTgknHistoryValue("2023-12-23", 99.D, 99.D, 99D, 2000D),
            buildTgknHistoryValue("2023-12-24", 100.D, 100.D, 100D, 1400D),
            buildImoexHistoryValue("2023-12-22", 2900D, 2900D, 1_000_000D),
            buildImoexHistoryValue("2023-12-23", 2900D, 2900D, 1_500_000D),
            buildImoexHistoryValue("2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initIntradayValues(
            buildImoexDelta( 1L, "10:00:00", 3000D, 1_000_000D),
            buildImoexDelta( 2L, "12:00:00", 3100D, 2_000_000D),
            buildTgknBuyDeal(1L,"10:00:00", 100D, 5000D, 1),
            buildTgknBuyDeal(2L,"10:03:00", 100D, 1000D, 1),
            buildTgknSellDeal(3L,"11:00:00", 100D, 1000D, 1),
            buildTgknBuyDeal(4L,"11:01:00", 100D, 1000D, 1),
            buildTgknBuyDeal(5L, "11:45:00", 102D, 5000D, 1)
        );
        commandBus().execute(new EnableUpdateInstrumentsCommand(getDatasourceId(), getTickers(getDatasourceId())));
        commandBus().execute(new IntegrateTradingDataCommand(getDatasourceId()));
        clearLogs();
    }

    private void prepareTestCase4() {
        final SignalScanner scanner = scannerRepository().findAllBy(getDatasourceId()).stream().findFirst().orElseThrow();
        final LocalDateTime today = LocalDateTime.parse("2023-12-25T12:00:00");
        initTodayDateTime("2023-12-25T12:00:00");
        scanner.getSignals().addAll(
            List.of(
                Signal.builder()
                    .price(10D)
                    .isBuy(true)
                    .isOpen(true)
                    .instrumentId(getInstrumentIdBy(TGKB))
                    .summary("summary")
                    .watermark(today)
                    .build(),
                Signal.builder()
                    .price(10D)
                    .isBuy(true)
                    .isOpen(true)
                    .instrumentId(getInstrumentIdBy(TGKN))
                    .summary("summary")
                    .watermark(today)
                    .build()
            )
        );
        initHistoryValues(
            buildTgkbHistoryValue("2023-12-22", 99.D, 99.D, 99D, 1000D),
            buildTgkbHistoryValue("2023-12-23", 99.D, 99.D, 99D, 2000D),
            buildTgkbHistoryValue("2023-12-24", 100.D, 100.D, 100D, 1400D),
            buildTgknHistoryValue("2023-12-22", 99.D, 99.1D, 97D, 2000D),
            buildTgknHistoryValue("2023-12-23", 99.D, 99.1D, 97D, 1000D),
            buildTgknHistoryValue("2023-12-24", 97.2D, 97.1D, 97D, 1500D),
            buildImoexHistoryValue("2023-12-22", 2900D, 2900D, 1_000_000D),
            buildImoexHistoryValue("2023-12-23", 2900D, 2900D, 1_500_000D),
            buildImoexHistoryValue("2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initIntradayValues(
            buildImoexDelta( 1L,"10:00:00", 3000D, 1_000_000D),
            buildImoexDelta( 2L,"12:00:00", 2900D, 2_000_000D),
            buildTgknBuyDeal(1L,"10:00:00", 98D, 5000D, 1),
            buildTgknSellDeal(2L,"10:03:00", 97D, 1000D, 1),
            buildTgknSellDeal(3L,"11:00:00", 98D, 1000D, 1),
            buildTgknSellDeal(4L,"11:01:00", 97D, 1000D, 1),
            buildTgknSellDeal(5L, "11:45:00", 96D, 5000D, 1),
            buildTgkbBuyDeal(1L,"10:00:00", 100D, 5000D, 1),
            buildTgkbBuyDeal(2L,"10:03:00", 100D, 1000D, 1),
            buildTgkbSellDeal(3L,"11:00:00", 100D, 1000D, 1),
            buildTgkbBuyDeal(4L,"11:01:00", 100D, 1000D, 1),
            buildTgkbBuyDeal(5L, "11:45:00", 102D, 5000D, 1)
        );
        commandBus().execute(new EnableUpdateInstrumentsCommand(getDatasourceId(), getTickers(getDatasourceId())));
        commandBus().execute(new IntegrateTradingDataCommand(getDatasourceId()));
        clearLogs();
    }

    private void prepareTestCase5() {
        initTodayDateTime("2023-12-25T12:00:00");
        initHistoryValues(
            buildTgknHistoryValue("2023-12-22", 99.D, 99.1D, 97D, 2000D),
            buildTgknHistoryValue("2023-12-23", 99.D, 99.1D, 97D, 1000D),
            buildTgknHistoryValue("2023-12-24", 97.2D, 97.1D, 97D, 1500D),
            buildImoexHistoryValue("2023-12-22", 2900D, 2900D, 1_000_000D),
            buildImoexHistoryValue("2023-12-23", 2900D, 2900D, 1_500_000D),
            buildImoexHistoryValue("2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initIntradayValues(
            buildImoexDelta( 1L,"10:00:00", 3000D, 1_000_000D),
            buildImoexDelta( 2L,"12:00:00", 2900D, 2_000_000D),
            buildTgknBuyDeal(1L,"10:00:00", 98D, 5000D, 1),
            buildTgknSellDeal(2L,"10:03:00", 97D, 1000D, 1),
            buildTgknSellDeal(3L,"11:00:00", 98D, 1000D, 1),
            buildTgknSellDeal(4L,"11:01:00", 97D, 1000D, 1),
            buildTgknSellDeal(5L, "11:45:00", 96D, 5000D, 1)
        );
        commandBus().execute(new EnableUpdateInstrumentsCommand(getDatasourceId(), getTickers(getDatasourceId())));
        commandBus().execute(new IntegrateTradingDataCommand(getDatasourceId()));
        clearLogs();
    }

    private void prepareTestCase6() {
        initTodayDateTime("2023-12-25T12:00:00");
        final LocalDateTime today = LocalDateTime.parse("2023-12-25T12:00:00");
        final SignalScanner scanner = scannerRepository().findAllBy(getDatasourceId()).stream().findFirst().orElseThrow();
        scanner.getSignals().add(
            Signal.builder()
                .price(10D)
                .isBuy(false)
                .isOpen(true)
                .instrumentId(getInstrumentIdBy(TGKN))
                .summary("summary")
                .watermark(today)
                .build()
        );
        initHistoryValues(
            buildTgknHistoryValue("2023-12-22", 99.D, 99.D, 99D, 1000D),
            buildTgknHistoryValue("2023-12-23", 99.D, 99.D, 99D, 2000D),
            buildTgknHistoryValue("2023-12-24", 100.D, 100.D, 100D, 1400D),
            buildImoexHistoryValue("2023-12-22", 2900D, 2900D, 1_000_000D),
            buildImoexHistoryValue("2023-12-23", 2900D, 2900D, 1_500_000D),
            buildImoexHistoryValue("2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initIntradayValues(
            buildImoexDelta(1L,"10:00:00", 3000D, 1_000_000D),
            buildImoexDelta( 2L,"12:00:00", 3100D, 2_000_000D),
            buildTgknBuyDeal(1L,"10:00:00", 100D, 5000D, 1),
            buildTgknBuyDeal(2L,"10:03:00", 100D, 1000D, 1),
            buildTgknSellDeal(3L,"11:00:00", 100D, 1000D, 1),
            buildTgknBuyDeal(4L,"11:01:00", 100D, 1000D, 1),
            buildTgknBuyDeal(5L,"11:45:00", 102D, 5000D, 1)
        );
        commandBus().execute(new EnableUpdateInstrumentsCommand(getDatasourceId(), getTickers(getDatasourceId())));
        commandBus().execute(new IntegrateTradingDataCommand(getDatasourceId()));
        clearLogs();
    }

    private Optional<SignalRegistered> findSignalFoundEvent() {
        return eventPublisher()
            .getEvents()
            .stream().filter(row -> row.getClass().equals(SignalRegistered.class))
            .findFirst()
            .map(SignalRegistered.class::cast);
    }
}
