package ru.ioque.investfund.application.modules.pipeline;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.IntradayJournal;
import ru.ioque.investfund.application.modules.risk.processor.RiskProcessor;
import ru.ioque.investfund.application.modules.scanner.processor.SignalProducer;

@Component
public class PipelineConfigurator {

    public PipelineConfigurator(
        SignalProducer signalProducer,
        RiskProcessor riskProcessor,
        IntradayJournal intradayJournal) {
        intradayJournal.subscribe(signalProducer);
        intradayJournal.subscribe(riskProcessor);
    }
}
