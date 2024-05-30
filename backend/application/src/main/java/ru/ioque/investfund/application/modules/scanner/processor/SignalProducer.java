package ru.ioque.investfund.application.modules.scanner.processor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.journal.AggregatedTotalsJournal;
import ru.ioque.investfund.application.adapters.journal.Processor;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.pipeline.SignalProducerContext;
import ru.ioque.investfund.domain.scanner.SignalRegistry;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;

@Component
@RequiredArgsConstructor
public class SignalProducer implements Processor<IntradayData> {

    @Getter
    private SignalProducerContext signalProducerContext;
    private SignalRegistry signalRegistry;

    private final DateTimeProvider dateTimeProvider;
    private final ScannerRepository scannerRepository;
    private final InstrumentRepository instrumentRepository;
    private final AggregatedTotalsJournal aggregatedTotalsJournal;
    private final SignalJournal signalJournal;

    public boolean isInit() {
        return signalProducerContext != null;
    }

    public void init(DatasourceId datasourceId) {
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
        this.signalProducerContext = new SignalProducerContext(scanners, instruments, aggregatedTotals);
        this.signalRegistry = new SignalRegistry(signals);
    }

    @Override
    public void process(IntradayData intradayData) {
        if (!signalProducerContext.containsTicker(intradayData.getTicker())) {
            return;
        }
        signalProducerContext.updateIntradayPerformance(intradayData);
        for (final SignalScanner scanner : signalProducerContext.getSubscribers(intradayData.getTicker())) {
            if (scanner.isActive()) {
                final LocalDateTime watermark = dateTimeProvider.nowDateTime();
                final List<InstrumentPerformance> instruments = scanner
                    .getInstrumentIds()
                    .stream()
                    .map(signalProducerContext::getInstrumentPerformance)
                    .filter(Objects::nonNull)
                    .toList();
                final List<Signal> foundedSignals = scanner.scanning(instruments, watermark);
                final List<Signal> registeredSignals = foundedSignals
                    .stream()
                    .filter(signalRegistry::register)
                    .toList();
                registeredSignals.forEach(signalJournal::publish);
            }
        }
    }
}
