package ru.ioque.investfund.application.adapters;

import ru.ioque.investfund.domain.scanner.financial.entity.Report;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository {
    void save(List<Report> reports);
    List<Report> getReportsBy(UUID scannerId);
    Optional<Report> getLastReportBy(UUID scannerId);
}
