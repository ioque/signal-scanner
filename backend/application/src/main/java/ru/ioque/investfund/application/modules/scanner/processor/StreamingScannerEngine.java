package ru.ioque.investfund.application.modules.scanner.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.application.modules.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.InstrumentPerformance;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SearchContext;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.WorkScannerReport;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StreamingScannerEngine {
    final SignalJournal signalJournal;
    final CommandJournal commandJournal;
    final ScannerRepository scannerRepository;
    final InstrumentRepository instrumentRepository;
    final DateTimeProvider dateTimeProvider;

    boolean active;
    SearchContext searchContext;
    Map<Ticker, List<ScannerId>> subscribers;
    List<SignalScanner> scanners;

    public void init(DatasourceId datasourceId) {
        this.scanners = scannerRepository.findAllBy(datasourceId);
        this.searchContext = new SearchContext(
            scanners
                .stream()
                .map(SignalScanner::getInstrumentIds)
                .flatMap(List::stream)
                .distinct()
                .map(instrumentRepository::getBy)
                .toList()
        );
        this.active = true;
    }

    @Async
    public void process(InstrumentPerformance instrumentPerformance) {
        Optional<Instrument> instrument = searchContext.getInstrumentBy(instrumentPerformance.getTicker());
        if (!active || instrument.isEmpty()) {
            return;
        }
        log.info("receive {}", instrumentPerformance);
        instrument.get().updatePerformance(instrumentPerformance);
        commandJournal.publish(new EvaluateEmulatedPosition(
            instrument.get().getId(),
            instrumentPerformance.getTodayLastPrice()
        ));
        scanners.stream()
            .filter(scanner -> scanner.getInstrumentIds().contains(instrument.get().getId()) && scanner.isActive())
            .forEach(scanner -> {
                final LocalDateTime watermark = dateTimeProvider.nowDateTime();
                log.info("scanner {} started scanning", scanner);
                final List<Instrument> instruments = scanner
                    .getInstrumentIds()
                    .stream()
                    .map(searchContext::getInstrumentBy)
                    .map(row -> row.orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
                final WorkScannerReport report = scanner.scanning(instruments, watermark);
                report.getRegisteredSignals().forEach(signalJournal::publish);
                log.info("result scanning: {}", report);
            });
    }
}
