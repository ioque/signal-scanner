package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.domain.exchange.event.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.scanner.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.entity.SignalScannerBot;
import ru.ioque.investfund.domain.scanner.value.Signal;

import java.util.Collection;
import java.util.List;

public class BaseScannerTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        eventBus().subscribe(TradingDataUpdatedEvent.class, dataScannerManager());
        loggerProvider().clearLogs();
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
        return getFinInstrumentByTicker("IMOEX");
    }

    protected FinInstrument getTgkb() {
        return getFinInstrumentByTicker("TGKB");
    }

    protected FinInstrument getTgkn() {
        return getFinInstrumentByTicker("TGKN");
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
