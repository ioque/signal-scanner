package ru.ioque.investfund.application.modules.pipeline;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.ioque.investfund.application.adapters.IntradayDataJournal;
import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.application.adapters.repository.AggregatedTotalsRepository;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.modules.pipeline.subscriber.RiskManager;
import ru.ioque.investfund.application.modules.pipeline.subscriber.SignalRegistry;
import ru.ioque.investfund.application.modules.pipeline.subscriber.SignalRegistryContext;
import ru.ioque.investfund.application.modules.pipeline.transformer.PerformanceCalculator;
import ru.ioque.investfund.application.modules.pipeline.transformer.PerformanceCalculatorContext;
import ru.ioque.investfund.application.modules.pipeline.transformer.SignalsFinder;
import ru.ioque.investfund.application.modules.pipeline.transformer.SignalsFinderContext;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Slf4j
@Component
@AllArgsConstructor
public class PipelineManager {

    private final SignalRepository signalRepository;
    private final ScannerRepository scannerRepository;
    private final AggregatedTotalsRepository aggregatedTotalsRepository;
    private final DatasourceRepository datasourceRepository;

    private final SignalRegistryContext signalRegistryContext;
    private final PerformanceCalculatorContext performanceCalculatorContext;
    private final SignalsFinderContext signalsFinderContext;

    private final IntradayDataJournal intradayDataJournal;
    private final PerformanceCalculator performanceCalculator;
    private final SignalsFinder signalsFinder;
    private final SignalRegistry signalRegistry;
    private final RiskManager riskManager;

    public void runPipeline() {
        log.info("pipeline started");
        final Flux<IntradayData> stream = intradayDataJournal.stream();

        stream
            .map(performanceCalculator::transform)
            .doOnError(throwable -> log.error("calc performance error", throwable))
            .map(signalsFinder::transform)
            .doOnError(throwable -> log.error("signals finder error", throwable))
            .flatMap(Flux::fromIterable)
            .subscribe(signalRegistry::receive);

        stream.subscribe(riskManager::receive);
    }

    public void initializeContexts() {
        final List<Datasource> datasources = datasourceRepository.getAll();
        final List<Instrument> instruments = datasources
            .stream()
            .map(Datasource::getInstruments)
            .flatMap(Collection::stream)
            .toList();
        final List<SignalScanner> scanners = datasources
            .stream()
            .map(datasource -> scannerRepository.findAllBy(datasource.getId()))
            .flatMap(Collection::stream)
            .toList();
        final List<AggregatedTotals> aggregatedTotals = scanners
            .stream()
            .map(SignalScanner::getInstrumentIds)
            .flatMap(Collection::stream)
            .map(aggregatedTotalsRepository::findAllBy)
            .flatMap(Collection::stream)
            .toList();
        final List<Signal> signals = signalRepository.getAll();
        performanceCalculatorContext.initialize();
        signalRegistryContext.initialize(signals);
        signalsFinderContext.initialize(scanners, instruments, aggregatedTotals);
        log.info("contexts initialized");
    }

    public void resetContexts() {
        performanceCalculatorContext.reset();
        signalRegistryContext.reset();
        signalsFinderContext.reset();
    }
}
