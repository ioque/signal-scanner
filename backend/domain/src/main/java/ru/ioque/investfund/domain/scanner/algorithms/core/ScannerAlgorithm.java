package ru.ioque.investfund.domain.scanner.algorithms.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.InstrumentTradingState;

import java.time.Instant;
import java.util.List;

@Getter
@EqualsAndHashCode
public abstract class ScannerAlgorithm {
    protected String name;
    public ScannerAlgorithm(String name) {
        this.name = name;
    }
    public abstract List<Signal> findSignals(List<InstrumentTradingState> instruments, Instant watermark);
}
