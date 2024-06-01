package ru.ioque.investfund.application.modules.pipeline.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.modules.pipeline.core.Transformer;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

@Component
@AllArgsConstructor
public class PerformanceCalculator implements Transformer<IntradayData, IntradayPerformance> {

    PerformanceCalculatorContext context;

    @Override
    public IntradayPerformance transform(IntradayData intradayData) {
        System.out.println("PerformanceCalculator");
        if (!context.isInitialized()) {
            throw new IllegalStateException("Context is not initialized");
        }
        final IntradayPerformance intradayPerformance = context
            .getPerformance(intradayData.getInstrumentId())
            .map(performance -> performance.add(intradayData))
            .orElse(IntradayPerformance.of(intradayData));
        context.savePerformance(intradayPerformance);
        return intradayPerformance;
    }
}
