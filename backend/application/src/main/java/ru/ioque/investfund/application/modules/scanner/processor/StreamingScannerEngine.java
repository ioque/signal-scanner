package ru.ioque.investfund.application.modules.scanner.processor;

import java.time.LocalDateTime;
import java.util.List;
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
import ru.ioque.investfund.application.modules.risk.command.EvaluateEmulatedPosition;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StreamingScannerEngine {
    final SearchContextManager searchContextManager;
    final SignalRegistrar signalRegistrar;
    final CommandJournal commandJournal;
    final DateTimeProvider dateTimeProvider;

    @Async
    public void process(IntradayPerformance intradayPerformance) {
        if (searchContextManager.getSearchContext() == null) {
            return;
        }
        Optional<InstrumentPerformance> instrument = searchContextManager
            .getSearchContext()
            .getInstrumentBy(intradayPerformance.getTicker());
        if (instrument.isEmpty()) {
            return;
        }
        log.info("receive {}", intradayPerformance);
        instrument.get().updatePerformance(intradayPerformance);
        commandJournal.publish(new EvaluateEmulatedPosition(
            instrument.get().getInstrumentId(),
            intradayPerformance.getTodayLastPrice()
        ));
        searchContextManager.getScanners().stream()
            .filter(scanner -> scanner.getInstrumentIds().contains(instrument.get().getInstrumentId()) && scanner.isActive())
            .forEach(scanner -> {
                final LocalDateTime watermark = dateTimeProvider.nowDateTime();
                log.info("scanner {} started scanning", scanner);
                final List<InstrumentPerformance> instruments = scanner
                    .getInstrumentIds()
                    .stream()
                    .map(searchContextManager.getSearchContext()::getInstrumentBy)
                    .map(row -> row.orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
                scanner.scanning(instruments, watermark).forEach(signalRegistrar::registerSignal);
            });
    }
}
