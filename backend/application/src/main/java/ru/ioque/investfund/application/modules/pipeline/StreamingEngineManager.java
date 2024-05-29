package ru.ioque.investfund.application.modules.pipeline;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.pipeline.PipelineContext;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

@Component
public class StreamingEngineManager {
    private PipelineContext pipelineContext;

    public void process(IntradayPerformance intradayPerformance) {

    }

    public void process(AggregatedTotals aggregatedTotals) {

    }
}
