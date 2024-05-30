package ru.ioque.investfund.application.modules.pipeline;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.AggregatedTotalsJournal;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@AllArgsConstructor
public class PipelineManager {
    @Getter
    PipelineContext pipelineContext;
    SignalRegistry signalRegistry;
    ScannerRepository scannerRepository;
    InstrumentRepository instrumentRepository;
    AggregatedTotalsJournal aggregatedTotalsJournal;
    SignalJournal signalJournal;

    public void initializePipeline(DatasourceId datasourceId) {
        final List<SignalScanner> scanners = scannerRepository.findAllBy(datasourceId);
        final List<Instrument> instruments = scanners
            .stream()
            .map(SignalScanner::getInstrumentIds)
            .flatMap(Collection::stream)
            .distinct()
            .map(instrumentRepository::getBy)
            .toList();
        final List<AggregatedTotals> aggregatedTotals = instruments
            .stream()
            .map(instrument -> aggregatedTotalsJournal.findAllBy(instrument.getTicker()))
            .flatMap(Collection::stream)
            .toList();
        final List<Signal> signals = scanners
            .stream()
            .map(scanner -> signalJournal.findAllBy(scanner.getId()))
            .flatMap(Collection::stream)
            .toList();
        pipelineContext.initialize(scanners, instruments, aggregatedTotals);
        signalRegistry.initialize(signals);
    }
}
