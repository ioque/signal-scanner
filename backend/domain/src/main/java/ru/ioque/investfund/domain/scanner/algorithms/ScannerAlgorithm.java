package ru.ioque.investfund.domain.scanner.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.ioque.investfund.domain.scanner.value.SignalSign;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@EqualsAndHashCode
public abstract class ScannerAlgorithm {
    protected String name;
    public ScannerAlgorithm(String name) {
        this.name = name;
    }
    public abstract List<SignalSign> run(List<TradingSnapshot> tradingSnapshots, LocalDateTime watermark);
}
