package ru.ioque.investfund.scanner.scanning;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.command.IntegrateTradingDataCommand;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.event.ScanningFinishedEvent;
import ru.ioque.investfund.domain.scanner.event.SignalFoundEvent;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProduceSignalCommandTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        initInstruments(imoex(), tgkb(), tgkn());
        commandBus().execute(new IntegrateInstrumentsCommand(getDatasourceId()));
        commandBus().execute(
            CreateScannerCommand.builder()
                .datasourceId(getDatasourceId())
                .workPeriodInMinutes(1)
                .tickers(List.of("TGKN", "TGKB", "IMOEX"))
                .description("description")
                .properties(
                    AnomalyVolumeProperties.builder()
                        .scaleCoefficient(1.5)
                        .indexTicker("IMOEX")
                        .historyPeriod(180)
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
        T4. Зафиксирован сигнал к покупке по тикеру - TGKN, алгоритм - аномальные объемы.
        """)
    void testCase4() {
        initTodayDateTime("2024-01-10T10:00:00");
        initTgknSellSignalDataset(getDatasourceId());

        commandBus().execute(new ProduceSignalCommand(getDatasourceId(), dateTimeProvider().nowDateTime()));

        final Optional<SignalFoundEvent> signalFoundEvent = findSignalFoundEvent();
        final Optional<ScanningFinishedEvent> scanningFinishedEvent = findScanningFinishedEvent();
        assertTrue(signalFoundEvent.isPresent());
        assertNotNull(signalFoundEvent.get().getId());
        assertEquals(getDatasourceId(), signalFoundEvent.get().getDatasourceId());
        assertEquals("TGKN", signalFoundEvent.get().getTicker());
        assertFalse(signalFoundEvent.get().isBuy());
        assertEquals(dateTimeProvider().nowDateTime(), signalFoundEvent.get().getWatermark());

        assertTrue(scanningFinishedEvent.isPresent());
        assertNotNull(scanningFinishedEvent.get().getId());
        assertEquals(getDatasourceId(), scanningFinishedEvent.get().getDatasourceId());
        assertEquals(dateTimeProvider().nowDateTime(), scanningFinishedEvent.get().getWatermark());
        assertEquals(dateTimeProvider().nowDateTime(), scanningFinishedEvent.get().getDateTime());
    }

    @Test
    @DisplayName("""
        T3. Ранее были зафиксированы сигналы к покупке по двум тикерам - TGKN, TGKB, алгоритм - аномальные объемы.
        По тикеру TGKN зафиксирован повторный сигнал к покупке, по тикеру TGKB зафиксирован сигнал к продаже.
        """)
    void testCase3() {
        initDealDatas(

        );
    }

    private void prepareState() {
        SignalScanner scanner = scannerRepository().findAllBy(getDatasourceId()).stream().findFirst().orElseThrow();

    }

    private void initTgknSellSignalDataset(UUID datasourceId) {
        initTradingResults(
            buildDealResultBy(datasourceId, "TGKN", "2023-12-22", 99.D, 99.1D, 97D, 2000D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-23", 99.D, 99.1D, 97D, 1000D),
            buildDealResultBy(datasourceId, "TGKN", "2023-12-24", 97.2D, 97.1D, 97D, 1500D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-22", 2900D, 2900D, 1_000_000D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-23", 2900D, 2900D, 1_500_000D),
            buildDeltaResultBy(datasourceId, "IMOEX", "2023-12-24", 3000D, 3000D, 2_000_000D)
        );
        initDealDatas(
            buildDeltaBy(datasourceId, 3L, "IMOEX", "10:00:00", 3000D, 1_000_000D),
            buildDeltaBy(datasourceId, 4L, "IMOEX", "12:00:00", 2900D, 2_000_000D),
            buildBuyDealBy(datasourceId, 6L, "TGKN", "10:00:00", 98D, 5000D, 1),
            buildSellDealBy(datasourceId, 7L, "TGKN", "10:03:00", 97D, 1000D, 1),
            buildSellDealBy(datasourceId, 8L, "TGKN", "11:00:00", 98D, 1000D, 1),
            buildSellDealBy(datasourceId, 9L, "TGKN", "11:01:00", 97D, 1000D, 1),
            buildSellDealBy(datasourceId, 10L, "TGKN", "11:45:00", 96D, 5000D, 1)
        );
        commandBus().execute(new EnableUpdateInstrumentsCommand(getDatasourceId(), getTickers(getDatasourceId())));
        commandBus().execute(new IntegrateTradingDataCommand(getDatasourceId()));
        clearLogs();
    }

    private Optional<SignalFoundEvent> findSignalFoundEvent() {
        return eventPublisher()
            .getEvents()
            .stream().filter(row -> row.getClass().equals(SignalFoundEvent.class))
            .findFirst()
            .map(SignalFoundEvent.class::cast);
    }

    private Optional<ScanningFinishedEvent> findScanningFinishedEvent() {
        return eventPublisher()
            .getEvents()
            .stream().filter(row -> row.getClass().equals(ScanningFinishedEvent.class))
            .findFirst()
            .map(ScanningFinishedEvent.class::cast);
    }
}
