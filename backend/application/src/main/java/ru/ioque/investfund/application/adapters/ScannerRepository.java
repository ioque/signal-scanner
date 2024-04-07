package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;

public interface ScannerRepository {
    void save(SignalScanner scanner);
    List<SignalScanner> getAll();
}
