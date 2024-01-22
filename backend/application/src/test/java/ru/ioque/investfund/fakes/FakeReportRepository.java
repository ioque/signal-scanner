package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ReportRepository;
import ru.ioque.investfund.domain.scanner.financial.entity.Report;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeReportRepository implements ReportRepository {
    public List<Report> reports = new CopyOnWriteArrayList<>();

    @Override
    public void save(List<Report> reports) {
        this.reports.addAll(reports);
    }

    @Override
    public List<Report> getReportsBy(UUID scannerId) {
        return reports.stream().filter(report -> report.getScannerId().equals(scannerId)).toList();
    }

    @Override
    public Optional<Report> getLastReportBy(UUID scannerId) {
        return getReportsBy(scannerId).stream().max(Report::compareTo);
    }
}
