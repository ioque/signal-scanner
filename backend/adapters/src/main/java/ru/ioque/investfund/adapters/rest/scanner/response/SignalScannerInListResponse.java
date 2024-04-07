package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerEntity;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalScannerInListResponse implements Serializable {
    UUID id;
    String description;
    Integer workPeriodInMinutes;
    Integer signalCounts;
    String lastExecutionDateTime;

    public static SignalScannerInListResponse from(ScannerEntity scanner) {
        return SignalScannerInListResponse.builder()
            .id(scanner.getId())
            .description(scanner.getDescription())
            .workPeriodInMinutes(scanner.getWorkPeriodInMinutes())
            .signalCounts(scanner.getSignals().size())
            .lastExecutionDateTime(
                scanner.getLastExecutionDateTime() == null
                    ? ""
                    : scanner.getLastExecutionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }
}
