package ru.ioque.investfund.application.modules.pipeline.transformer;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.modules.pipeline.core.Context;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.value.IntradayPerformance;

@Component
public class PerformanceCalculatorContext implements Context {

    @Getter
    private boolean initialized = false;
    private final Map<InstrumentId, IntradayPerformance> performances = new ConcurrentHashMap<>();

    public synchronized void initialize() {
        this.initialized = true;
    }

    @Override
    public synchronized void reset() {
        this.initialized = false;
        this.performances.clear();
    }

    public Optional<IntradayPerformance> getPerformance(InstrumentId instrumentId) {
        return Optional.ofNullable(performances.get(instrumentId));
    }

    public void savePerformance(IntradayPerformance instrumentPerformance) {
        performances.put(instrumentPerformance.getInstrumentId(), instrumentPerformance);
    }
}
