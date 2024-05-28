package ru.ioque.investfund.application.modules.scanner.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.CommandJournal;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.application.modules.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.value.SearchContext;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.WorkScannerReport;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StreamingScannerEngine {
    final SearchContextBuilder searchContextBuilder;
    final SignalJournal signalJournal;
    final CommandJournal commandJournal;
    final DateTimeProvider dateTimeProvider;

    @Getter
    SearchContext searchContext;
    Map<Ticker, List<ScannerId>> subscribers;
    List<SignalScanner> scanners;

    public void init(List<SignalScanner> scanners) {
        this.scanners = scanners;
        this.searchContext = searchContextBuilder.build(scanners);
    }

    @Async
    public void process(IntradayPerformance intradayPerformance) {
        if (searchContext == null) {
            return;
        }
        Optional<InstrumentPerformance> instrument = searchContext.getInstrumentBy(intradayPerformance.getTicker());
        if (instrument.isEmpty()) {
            return;
        }
        log.info("receive {}", intradayPerformance);
        instrument.get().updatePerformance(intradayPerformance);
        commandJournal.publish(new EvaluateEmulatedPosition(
            instrument.get().getInstrumentId(),
            intradayPerformance.getTodayLastPrice()
        ));
        scanners.stream()
            .filter(scanner -> scanner.getInstrumentIds().contains(instrument.get().getInstrumentId()) && scanner.isActive())
            .forEach(scanner -> {
                final LocalDateTime watermark = dateTimeProvider.nowDateTime();
                log.info("scanner {} started scanning", scanner);
                final List<InstrumentPerformance> instruments = scanner
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
