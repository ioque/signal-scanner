package ru.ioque.investfund.scanner.scanning;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;

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
            .map(scanner -> signalJournal().findAllBy(scanner.getId()))
            .flatMap(Collection::stream)
            .toList();
    }

    protected InstrumentPerformance getImoexPerformance() {
        return getInstrumentPerformanceBy(IMOEX);
    }

    protected InstrumentPerformance getTgkbPerformance() {
        return getInstrumentPerformanceBy(TGKB);
    }

    protected InstrumentPerformance getTgknPerformance() {
        return getInstrumentPerformanceBy(TGKN);
    }

    protected InstrumentPerformance getTatnPerformance() {
        return getInstrumentPerformanceBy(TATN);
    }

    protected InstrumentPerformance getBrf4Performance() {
        return getInstrumentPerformanceBy(BRF4);
    }

    protected InstrumentPerformance getRosnPerformance() {
        return getInstrumentPerformanceBy(ROSN);
    }

    protected InstrumentPerformance getLkohPerformance() {
        return getInstrumentPerformanceBy(LKOH);
    }

    protected InstrumentPerformance getSibnPerformance() {
        return getInstrumentPerformanceBy(SIBN);
    }

    protected InstrumentPerformance getSberPerformance() {
        return getInstrumentPerformanceBy(SBER);
    }

    protected InstrumentPerformance getSberpPerformance() {
        return getInstrumentPerformanceBy(SBERP);
    }

    protected InstrumentPerformance getInstrumentPerformanceBy(String ticker) {
        final PipelineContext pipelineContext = pipelineManager().getPipelineContext();
        return pipelineContext.getInstrumentPerformance(pipelineContext.findInstrumentId(new Ticker(ticker)));
    }
}
