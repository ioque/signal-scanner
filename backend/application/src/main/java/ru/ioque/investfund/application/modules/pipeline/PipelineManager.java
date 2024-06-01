package ru.ioque.investfund.application.modules.pipeline;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.application.adapters.repository.AggregatedTotalsRepository;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.modules.pipeline.sink.SignalRegistryContext;
import ru.ioque.investfund.application.modules.pipeline.transformer.PerformanceCalculatorContext;
import ru.ioque.investfund.application.modules.pipeline.transformer.SignalsFinderContext;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

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
    }

    public void resetContexts() {
        performanceCalculatorContext.reset();
        signalRegistryContext.reset();
        signalsFinderContext.reset();
    }
}
