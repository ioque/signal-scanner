package ru.ioque.investfund.domain.scanner.value.algorithms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.ScanningResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public abstract class ScannerAlgorithm {
    protected String name;
    public ScannerAlgorithm(String name) {
        this.name = name;
    }
    public abstract ScanningResult run(UUID scannerId, List<TradingSnapshot> tradingSnapshots, LocalDateTime dateTimeNow);
}