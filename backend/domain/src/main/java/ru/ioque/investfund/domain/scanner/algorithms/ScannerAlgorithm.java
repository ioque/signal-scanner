package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@EqualsAndHashCode
public abstract class ScannerAlgorithm {
    protected String name;
    public ScannerAlgorithm(String name) {
        this.name = name;
    }
    public abstract List<Signal> findSignals(List<Instrument> instruments, LocalDateTime watermark);
}
