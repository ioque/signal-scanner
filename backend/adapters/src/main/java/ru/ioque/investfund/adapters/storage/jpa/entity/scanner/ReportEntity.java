package ru.ioque.investfund.adapters.storage.jpa.entity.scanner;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.financial.entity.Report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report")
@Entity(name = "Report")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    UUID scannerId;
    LocalDateTime time;

    @Setter
    @ToString.Exclude
    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<ReportLogEntity> logs;

    @Setter
    @ToString.Exclude
    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<SignalEntity> signals;

    public Report toDomain() {
        return Report
            .builder()
            .scannerId(scannerId)
            .time(time)
            .logs(logs.stream().map(ReportLogEntity::toDomain).toList())
            .signals(signals.stream().map(SignalEntity::toDomain).toList())
            .build();
    }
}
