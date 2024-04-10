package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.AddDatasourceCommand;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseScannerTest extends BaseTest {
    protected static final String BRF4 = "BRF4";
    protected static final String TATN = "TATN";
    protected static final String TGKN = "TGKN";
    protected static final String TGKB = "TGKB";
    protected static final String IMOEX = "IMOEX";
    protected static final String ROSN = "ROSN";
    protected static final String LKOH = "LKOH";
    protected static final String SIBN = "SIBN";
    protected static final String SBER = "SBER";
    protected static final String SBERP = "SBERP";
    @BeforeEach
    void beforeEach() {
        datasourceManager().registerDatasource(
            AddDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        eventBus().subscribe(TradingDataUpdatedEvent.class, scannerManager());
        loggerProvider().clearLogs();
    }

    protected UUID getDatasourceId() {
        return datasourceRepository().getAll().get(0).getId();
    }

    protected void runWorkPipelineAndClearLogs() {
        datasourceManager().execute();
        loggerProvider().clearLogs();
    }

    protected void assertSignals(List<Signal> signals, int allSize, int buySize, int sellSize) {
        assertEquals(allSize, signals.size());
        assertEquals(buySize, signals.stream().filter(Signal::isBuy).count());
        assertEquals(sellSize, signals.stream().filter(row -> !row.isBuy()).count());
    }

    protected List<Signal> getSignals() {
        return scannerRepository()
            .getAll()
            .stream()
            .map(SignalScanner::getSignals)
            .flatMap(Collection::stream)
            .toList();
    }

    protected TradingSnapshot getImoex() {
        return getSnapshotBy(IMOEX);
    }

    protected TradingSnapshot getTgkb() {
        return getSnapshotBy(TGKB);
    }

    protected TradingSnapshot getTgkn() {
        return getSnapshotBy(TGKN);
    }

    protected TradingSnapshot getTatn() {
        return getSnapshotBy(TATN);
    }

    protected TradingSnapshot getBrf4() {
        return getSnapshotBy(BRF4);
    }
    protected TradingSnapshot getRosn() {
        return getSnapshotBy(ROSN);
    }
    protected TradingSnapshot getLkoh() {
        return getSnapshotBy(LKOH);
    }
    protected TradingSnapshot getSibn() {
        return getSnapshotBy(SIBN);
    }

    protected TradingSnapshot getSber() {
        return getSnapshotBy(SBER);
    }

    protected TradingSnapshot getSberp() {
        return getSnapshotBy(SBERP);
    }

    protected TradingSnapshot getSnapshotBy(String ticker) {
        return scannerRepository()
            .getAll()
            .stream()
            .map(SignalScanner::getTradingSnapshots)
            .flatMap(Collection::stream)
            .filter(row -> row.getTicker().equals(ticker))
            .findFirst()
            .orElseThrow();
    }
}
