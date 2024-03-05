package ru.ioque.investfund.adapters.storage.jpa.entity.scanner;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.financial.entity.ScannerLog;

import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report_log")
@Entity(name = "ReportLog")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ToString.Exclude
    @JoinColumn(name="report_id")
    @ManyToOne(fetch = FetchType.LAZY)
    ReportEntity report;

    String message;
    Instant time;

    public ScannerLog toDomain() {
        return new ScannerLog(message, time);
    }
}
