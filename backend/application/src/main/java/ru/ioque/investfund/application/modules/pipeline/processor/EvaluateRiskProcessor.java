package ru.ioque.investfund.application.modules.pipeline.processor;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.journal.Processor;
import ru.ioque.investfund.application.modules.pipeline.PipelineContext;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.position.EmulatedPosition;

@Component
@AllArgsConstructor
public class EvaluateRiskProcessor implements Processor<IntradayData> {
    PipelineContext pipelineContext;

    @Override
    public void process(IntradayData intradayData) {
        InstrumentId instrumentId = pipelineContext.findInstrumentId(intradayData.getTicker());
        List<EmulatedPosition> emulatedPositions = pipelineContext.getAllEmulatedPositionBy(instrumentId);
        for (EmulatedPosition emulatedPosition : emulatedPositions) {
            if (emulatedPosition.evaluateProfit(intradayData.getPrice()) < -5) {
                System.out.println("ALARM!");
            }
        }
    }
}
