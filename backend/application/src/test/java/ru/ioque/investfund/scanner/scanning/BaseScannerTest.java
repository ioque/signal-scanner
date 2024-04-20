package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
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
            CreateDatasourceCommand.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        loggerProvider().clearLogs();
    }

    protected void assertSignals(List<Signal> signals, int allSize, int openSize, int buySize, int sellSize) {
        assertEquals(allSize, signals.size());
        assertEquals(openSize, signals.stream().filter(Signal::isOpen).count());
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

    protected TradingSnapshot getImoex() {
        return getSnapshotBy(imoexId);
    }

    protected TradingSnapshot getTgkb() {
        return getSnapshotBy(tgkbId);
    }

    protected TradingSnapshot getTgkn() {
        return getSnapshotBy(tgknId);
    }

    protected TradingSnapshot getTatn() {
        return getSnapshotBy(tatnId);
    }

    protected TradingSnapshot getBrf4() {
        return getSnapshotBy(brf4Id);
    }
    protected TradingSnapshot getRosn() {
        return getSnapshotBy(rosnId);
    }
    protected TradingSnapshot getLkoh() {
        return getSnapshotBy(lkohId);
    }
    protected TradingSnapshot getSibn() {
        return getSnapshotBy(sibnId);
    }

    protected TradingSnapshot getSber() {
        return getSnapshotBy(sberId);
    }

    protected TradingSnapshot getSberp() {
        return getSnapshotBy(sberpId);
    }

    protected TradingSnapshot getSnapshotBy(InstrumentId instrumentId) {
        return tradingDataRepository().findAllBy(List.of(instrumentId)).get(0);
    }
}
