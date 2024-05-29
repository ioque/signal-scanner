package ru.ioque.investfund.application.modules.scanner;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.journal.AggregatedTotalsJournal;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.pipeline.SignalScannerContext;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;

@Component
@RequiredArgsConstructor
public class SignalScannerProcessor {
    @Getter
    private SignalScannerContext signalScannerContext;

    private final SignalRegistrar signalRegistrar;
    private final DateTimeProvider dateTimeProvider;
    private final ScannerRepository scannerRepository;
    private final InstrumentRepository instrumentRepository;
    private final AggregatedTotalsJournal aggregatedTotalsJournal;

    public synchronized boolean isInit() {
        return signalScannerContext != null;
    }

    public synchronized void init(DatasourceId datasourceId) {
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
        this.signalScannerContext = new SignalScannerContext(scanners, instruments, aggregatedTotals);
    }

    public synchronized void process(IntradayData intradayData) {
        if (!signalScannerContext.containsTicker(intradayData.getTicker())) {
            return;
        }
        signalScannerContext.updateIntradayPerformance(intradayData);
        for (final SignalScanner scanner : signalScannerContext.getSubscribers(intradayData.getTicker())) {
            if (scanner.isActive()) {
                final LocalDateTime watermark = dateTimeProvider.nowDateTime();
                final List<InstrumentPerformance> instruments = scanner
                    .getInstrumentIds()
                    .stream()
                    .map(signalScannerContext::getInstrumentPerformance)
                    .filter(Objects::nonNull)
                    .toList();
                final List<Signal> foundedSignals = scanner.scanning(instruments, watermark);
                for (final Signal signal : foundedSignals) {
                    signalRegistrar.registerSignal(signal);
                };
            }
        }
    }
}
