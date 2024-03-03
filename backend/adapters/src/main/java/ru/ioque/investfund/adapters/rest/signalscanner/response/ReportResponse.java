package ru.ioque.investfund.adapters.rest.signalscanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ReportEntity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponse implements Serializable {
    LocalDate dateTime;
    List<ReportLogResponse> logs;

    public static ReportResponse from(ReportEntity reportEntity) {
        return ReportResponse.builder()
            .dateTime(reportEntity.getTime().toLocalDate())
            .logs(reportEntity.getLogs().stream().map(ReportLogResponse::from).toList())
            .build();
    }
}
