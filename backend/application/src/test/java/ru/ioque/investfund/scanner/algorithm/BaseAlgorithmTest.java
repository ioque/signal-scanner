package ru.ioque.investfund.scanner.algorithm;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.PublishAggregatedHistory;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.BRF4;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.IMOEX;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.LKOH;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.ROSN;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.SBER;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.SBERP;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.SIBN;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.TATN;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.TGKB;
import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.TGKN;

public class BaseAlgorithmTest extends BaseTest {
    @BeforeEach
    void beforeEach() {
        commandBus().execute(
            CreateDatasource.builder()
                .name("Московская биржа")
                .description("Московская биржа")
                .url("http://localhost:8080")
                .build()
        );
        intradayJournal().subscribe(signalScannerProcessor());
        loggerProvider().clearLogs();
    }

    protected void runPipeline(DatasourceId datasourceId) {
        commandBus().execute(new PublishAggregatedHistory(datasourceId));
        pipelineManager().initializePipeline(datasourceId);
        commandBus().execute(new PublishIntradayData(datasourceId));
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
