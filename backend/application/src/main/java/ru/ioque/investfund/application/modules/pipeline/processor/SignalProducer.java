package ru.ioque.investfund.application.modules.pipeline.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.pipeline.core.Processor;
import ru.ioque.investfund.application.adapters.journal.SignalJournal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.application.modules.pipeline.SignalRegistry;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;

@Component
@RequiredArgsConstructor
public class SignalProducer implements Processor<IntradayData> {

    private final PipelineContext pipelineContext;
    private final SignalRegistry signalRegistry;
    private final DateTimeProvider dateTimeProvider;
    private final SignalJournal signalJournal;

    @Override
    public void process(IntradayData intradayData) {
        pipelineContext.updateIntradayPerformance(intradayData);
        for (final SignalScanner scanner : pipelineContext.getSubscribers(intradayData.getTicker())) {
            if (scanner.isActive()) {
                final LocalDateTime watermark = dateTimeProvider.nowDateTime();
                final List<InstrumentPerformance> instruments = scanner
                    .getInstrumentIds()
                    .stream()
                    .map(pipelineContext::getInstrumentPerformance)
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
