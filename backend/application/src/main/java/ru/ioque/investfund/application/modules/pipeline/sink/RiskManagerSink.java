package ru.ioque.investfund.application.modules.pipeline.sink;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.modules.pipeline.core.Sink;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Component
@AllArgsConstructor
public class RiskManagerSink implements Sink<IntradayData> {
    @Override
    public void consume(IntradayData intradayData) {
//        InstrumentId instrumentId = pipelineContext.findInstrumentId(intradayData.getTicker());
//        List<Position> positions = pipelineContext.getAllEmulatedPositionBy(instrumentId);
//        for (Position position : positions) {
//            if (position.evaluateProfit(intradayData.getPrice()) < -5) {
//                System.out.println("ALARM!");
//            }
//        }
    }
}
