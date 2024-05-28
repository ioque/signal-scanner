package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.value.InstrumentPerformance;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@EqualsAndHashCode
public abstract class ScannerAlgorithm {
    protected String name;
    public ScannerAlgorithm(String name) {
        this.name = name;
    }
    public abstract List<Signal> findSignals(List<InstrumentPerformance> instruments, LocalDateTime watermark);
}
