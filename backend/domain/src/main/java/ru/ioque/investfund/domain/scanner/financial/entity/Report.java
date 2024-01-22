package ru.ioque.investfund.domain.scanner.financial.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Report implements Comparable<Report> {
    UUID scannerId;
    LocalDateTime time;
    List<ReportLog> logs;
    List<Signal> signals;

    @Override
    public int compareTo(Report report) {
        return time.compareTo(report.getTime());
    }
}
