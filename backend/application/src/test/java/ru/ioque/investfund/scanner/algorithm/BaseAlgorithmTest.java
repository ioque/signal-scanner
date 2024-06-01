package ru.ioque.investfund.scanner.algorithm;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasource;
import ru.ioque.investfund.application.modules.datasource.command.PublishAggregatedHistory;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.InstrumentTradingState;

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
        buildDefaultTopology();
        loggerProvider().clearLogs();
    }

    protected void runPipeline(DatasourceId datasourceId) {
        commandBus().execute(new PublishAggregatedHistory(datasourceId));
        pipelineManager().start();
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

    protected InstrumentTradingState getImoexPerformance() {
        return getInstrumentPerformanceBy(IMOEX);
    }

    protected InstrumentTradingState getTgkbPerformance() {
        return getInstrumentPerformanceBy(TGKB);
    }

    protected InstrumentTradingState getTgknPerformance() {
        return getInstrumentPerformanceBy(TGKN);
    }

    protected InstrumentTradingState getTatnPerformance() {
        return getInstrumentPerformanceBy(TATN);
    }

    protected InstrumentTradingState getBrf4Performance() {
        return getInstrumentPerformanceBy(BRF4);
    }

    protected InstrumentTradingState getRosnPerformance() {
        return getInstrumentPerformanceBy(ROSN);
    }

    protected InstrumentTradingState getLkohPerformance() {
        return getInstrumentPerformanceBy(LKOH);
    }

    protected InstrumentTradingState getSibnPerformance() {
        return getInstrumentPerformanceBy(SIBN);
    }

    protected InstrumentTradingState getSberPerformance() {
        return getInstrumentPerformanceBy(SBER);
    }

    protected InstrumentTradingState getSberpPerformance() {
        return getInstrumentPerformanceBy(SBERP);
    }

    protected InstrumentTradingState getInstrumentPerformanceBy(String ticker) {
        return signalsFinderContext().getTradingState(getInstrumentId(ticker)).orElseThrow();
    }
}
