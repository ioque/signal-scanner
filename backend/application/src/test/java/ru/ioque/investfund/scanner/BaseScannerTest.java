package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.exchange.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.SignalScannerBot;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.util.Collection;
import java.util.List;

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
    @BeforeEach
    void beforeEach() {
        eventBus().subscribe(TradingDataUpdatedEvent.class, dataScannerManager());
        loggerProvider().clearLogs();
    }

    protected void assertSignals(List<Signal> signals, int allSize, int buySize, int sellSize) {
        assertEquals(allSize, signals.size());
        assertEquals(buySize, signals.stream().filter(Signal::isBuy).count());
        assertEquals(sellSize, signals.stream().filter(row -> !row.isBuy()).count());
    }

    protected void runWorkPipline() {
        exchangeManager().execute();
    }

    protected List<Signal> getSignals() {
        return fakeDataScannerStorage()
            .getAll()
            .stream()
            .map(SignalScannerBot::getSignals)
            .flatMap(Collection::stream)
            .toList();
    }

    protected FinInstrument getImoex() {
        return getFinInstrumentByTicker(IMOEX);
    }

    protected FinInstrument getTgkb() {
        return getFinInstrumentByTicker(TGKB);
    }

    protected FinInstrument getTgkn() {
        return getFinInstrumentByTicker(TGKN);
    }

    protected FinInstrument getTatn() {
        return getFinInstrumentByTicker(TATN);
    }

    protected FinInstrument getBrf4() {
        return getFinInstrumentByTicker(BRF4);
    }
    protected FinInstrument getRosn() {
        return getFinInstrumentByTicker(ROSN);
    }
    protected FinInstrument getLkoh() {
        return getFinInstrumentByTicker(LKOH);
    }
    protected FinInstrument getSibn() {
        return getFinInstrumentByTicker(SIBN);
    }
    protected FinInstrument getFinInstrumentByTicker(String ticker) {
        return fakeDataScannerStorage()
            .getAll()
            .stream()
            .map(SignalScannerBot::getFinInstruments)
            .flatMap(Collection::stream)
            .filter(row -> row.getTicker().equals(ticker))
            .findFirst()
            .orElseThrow();
    }
}
