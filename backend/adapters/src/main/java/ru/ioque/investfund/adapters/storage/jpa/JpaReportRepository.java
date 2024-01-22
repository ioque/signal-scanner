package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ReportEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ReportLogEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ReportEntityRepository;
import ru.ioque.investfund.application.adapters.ReportRepository;
import ru.ioque.investfund.domain.scanner.financial.entity.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JpaReportRepository implements ReportRepository {
    ReportEntityRepository repository;

    @Override
    public void save(List<Report> reports) {
        repository.saveAll(reports.stream().map(this::toEntity).toList());
    }

    @Override
    public List<Report> getReportsBy(UUID scannerId) {
        return repository.findAllByScannerId(scannerId).stream().map(ReportEntity::toDomain).toList();
    }

    @Override
    public Optional<Report> getLastReportBy(UUID scannerId) {
        return Optional.empty();
    }

    private ReportEntity toEntity(Report report) {
        ReportEntity reportEntity = ReportEntity.builder()
            .time(report.getTime())
            .scannerId(report.getScannerId())
            .logs(new ArrayList<>())
            .signals(new ArrayList<>())
            .build();
        List<ReportLogEntity> reportLogEntities = report
            .getLogs()
            .stream()
            .map(log -> ReportLogEntity
                .builder()
                .report(reportEntity)
                .message(log.getMessage())
                .time(log.getTime())
                .build()
            )
            .toList();
        List<SignalEntity> signalEntities = report
            .getSignals()
            .stream()
            .map(row -> SignalEntity.builder()
                .instrumentId(row.getInstrumentId())
                .isBuy(row.isBuy())
                .report(reportEntity)
                .build()
            )
            .toList();
        reportEntity.setSignals(signalEntities);
        reportEntity.setLogs(reportLogEntities);
        return reportEntity;
    }
}
