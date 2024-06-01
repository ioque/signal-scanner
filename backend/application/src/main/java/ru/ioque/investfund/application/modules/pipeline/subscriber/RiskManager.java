package ru.ioque.investfund.application.modules.pipeline.subscriber;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.application.modules.pipeline.core.Subscriber;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.event.SignalAutoClosed;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RiskManager implements Subscriber<IntradayData> {
    SignalRegistryContext context;
    SignalRepository signalRepository;

    @Override
    public void receive(IntradayData intradayData) {
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
