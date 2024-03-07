package ru.ioque.investfund.domain.scanner.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public abstract class SignalAlgorithm {
    protected String name;
    public SignalAlgorithm(String name) {
        this.name = name;
    }
    public abstract ScanningResult run(UUID scannerId, List<FinInstrument> finInstruments, LocalDateTime dateTimeNow);
}
