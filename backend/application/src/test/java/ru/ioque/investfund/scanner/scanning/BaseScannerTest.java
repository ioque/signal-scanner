package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

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

    protected Instrument getImoex() {
        return getInstrumentBy(IMOEX);
    }

    protected Instrument getTgkb() {
        return getInstrumentBy(TGKB);
    }

    protected Instrument getTgkn() {
        return getInstrumentBy(TGKN);
    }

    protected Instrument getTatn() {
        return getInstrumentBy(TATN);
    }

    protected Instrument getBrf4() {
        return getInstrumentBy(BRF4);
    }

    protected Instrument getRosn() {
        return getInstrumentBy(ROSN);
    }

    protected Instrument getLkoh() {
        return getInstrumentBy(LKOH);
    }

    protected Instrument getSibn() {
        return getInstrumentBy(SIBN);
    }

    protected Instrument getSber() {
        return getInstrumentBy(SBER);
    }

    protected Instrument getSberp() {
        return getInstrumentBy(SBERP);
    }

    protected Instrument getInstrumentBy(String ticker) {
        return datasourceRepository()
            .getAll()
            .stream()
            .map(Datasource::getInstruments)
            .flatMap(Collection::stream)
            .filter(row -> row.getTicker().equals(new Ticker(ticker))).findFirst()
            .orElseThrow();
    }
}
