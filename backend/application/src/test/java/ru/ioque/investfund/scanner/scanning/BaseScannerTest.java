package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseScannerTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        loggerProvider().clearLogs();
    }

    protected void assertSignals(List<Signal> signals, int size, int buySize, int sellSize) {
        assertEquals(size, signals.size());
        assertEquals(buySize, signals.stream().filter(Signal::isBuy).count());
        assertEquals(sellSize, signals.stream().filter(row -> !row.isBuy()).count());
    }

    protected List<Signal> getSignals() {
        return scannerRepository()
            .findAllBy(getDatasourceId())
            .stream()
            .map(SignalScanner::getSignals)
            .flatMap(Collection::stream)
            .toList();
    }

    protected TradingSnapshot getImoexSnapshot() {
        return getSnapshotBy(IMOEX);
    }

    protected TradingSnapshot getTgkbSnapshot() {
        return getSnapshotBy(TGKB);
    }

    protected TradingSnapshot getTgknSnapshot() {
        return getSnapshotBy(TGKN);
    }

    protected TradingSnapshot getTatnSnapshot() {
        return getSnapshotBy(TATN);
    }

    protected TradingSnapshot getBrf4Snapshot() {
        return getSnapshotBy(BRF4);
    }

    protected TradingSnapshot getRosnSnapshot() {
        return getSnapshotBy(ROSN);
    }

    protected TradingSnapshot getLkohSnapshot() {
        return getSnapshotBy(LKOH);
    }

    protected TradingSnapshot getSibnSnapshot() {
        return getSnapshotBy(SIBN);
    }

    protected TradingSnapshot getSberSnapshot() {
        return getSnapshotBy(SBER);
    }

    protected TradingSnapshot getSberpSnapshot() {
        return getSnapshotBy(SBERP);
    }

    protected TradingSnapshot getSnapshotBy(String ticker) {
        Instrument instrument = datasourceRepository()
            .getAll()
            .stream()
            .map(Datasource::getInstruments)
            .flatMap(Collection::stream)
            .filter(row -> row.getTicker().equals(new Ticker(ticker))).findFirst()
            .orElseThrow();
        return tradingDataRepository().findAllBy(List.of(instrument.getId())).get(0);
    }
}
