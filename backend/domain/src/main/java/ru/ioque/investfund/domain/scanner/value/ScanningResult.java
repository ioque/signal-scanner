package ru.ioque.investfund.domain.scanner.value;

import lombok.AllArgsConstructor;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class ScanningResult {
    private final Set<Signal> signals = new HashSet<>();
    private final Set<String> logs = new HashSet<>();

    public void addSignal(Signal signal) {
        signals.add(signal);
    }

    public void addLog(String log) {
        logs.add(log);
    }

    public List<Signal> getSignals() {
        return signals.stream().toList();
    }

    public List<String> getLogs() {
        return logs.stream().toList();
    }
}
