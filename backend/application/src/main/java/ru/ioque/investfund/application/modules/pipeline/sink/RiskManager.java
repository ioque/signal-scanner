package ru.ioque.investfund.application.modules.pipeline.sink;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.application.modules.pipeline.core.Sink;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.event.SignalAutoClosed;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RiskManager implements Sink<IntradayData> {
    SignalRegistryContext context;
    SignalRepository signalRepository;

    @Override
    public void consume(IntradayData intradayData) {
        System.out.println("RiskManager");
        List<Signal> signals = context.getSignalsBy(intradayData.getInstrumentId());
        for (Signal signal : signals) {
            if (signal.evaluateProfit(intradayData.getPrice()) < -15) {
                signal.close(intradayData.getPrice());
                signalRepository.save(
                    SignalAutoClosed.builder()
                        .signalId(signal.getId())
                        .closePrice(intradayData.getPrice())
                        .build()
                );
            }
        }
    }
}
