package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.ReportRepository;
import ru.ioque.investfund.domain.scanner.financial.entity.Report;
import ru.ioque.investfund.domain.scanner.financial.entity.ReportLog;
import ru.ioque.investfund.domain.scanner.financial.entity.Signal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JPA_REPORT_REPO")
public class JpaReportRepositoryTest extends BaseJpaTest {
    ReportRepository reportRepository;

    public JpaReportRepositoryTest(
        @Autowired ReportRepository reportRepository
    ) {
        this.reportRepository = reportRepository;
    }

    @Test
    @DisplayName("T1. Сохранение списка отчетов.")
    void testCase1() {
        final UUID scannerId = UUID.randomUUID();
        Report report = Report.builder()
            .scannerId(scannerId)
            .time(LocalDateTime.now())
            .logs(List.of(new ReportLog("test", Instant.now().truncatedTo(ChronoUnit.SECONDS))))
            .signals(List.of(new Signal(UUID.randomUUID(), true)))
            .build();

        reportRepository.save(List.of(report));

        assertEquals(1, reportRepository.getReportsBy(scannerId).size());
        assertEquals(report, reportRepository.getReportsBy(scannerId).get(0));
    }
}
