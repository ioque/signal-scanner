package ru.ioque.investfund.application.modules.pipeline.transformer;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.modules.pipeline.core.Transformer;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

@Component
@AllArgsConstructor
public class SignalsFinder implements Transformer<IntradayPerformance, List<Signal>> {
    private final SignalsFinderContext context;

    @Override
    public List<Signal> transform(IntradayPerformance intradayPerformance) {
        if (!context.isInitialized()) {
            throw new IllegalStateException("Context is not initialized");
        }
        final List<Signal> signals = new ArrayList<>();
        final InstrumentId instrumentId = intradayPerformance.getInstrumentId();
        context
            .getTradingState(instrumentId)
            .ifPresent(tradingState -> context.updateTradingState(tradingState.recalc(intradayPerformance)));
        for (final SignalScanner scanner : context.findScannersBy(instrumentId)) {
            if (scanner.isActive()) {
                signals
                    .addAll(
                        scanner
                            .scanning(
                                context.findTradingStatesBy(scanner.getInstrumentIds()),
                                intradayPerformance.getTimestamp()
                            )
                    );
            }
        }
        return signals;
    }
}
