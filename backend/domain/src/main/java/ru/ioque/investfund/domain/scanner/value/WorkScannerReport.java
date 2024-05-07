package ru.ioque.investfund.domain.scanner.value;

import lombok.Getter;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WorkScannerReport {
    List<Signal> existedSignals = new ArrayList<>();
    List<Signal> foundedSignals = new ArrayList<>();
    List<Signal> registeredSignals = new ArrayList<>();

    public void addExistedSignals(List<Signal> signal) {
        this.existedSignals.addAll(signal);
    }

    public void addFoundedSignals(List<Signal> signal) {
        this.foundedSignals.addAll(signal);
    }

    public void addRegisteredSignals(List<Signal> signal) {
        this.registeredSignals.addAll(signal);
    }
}
