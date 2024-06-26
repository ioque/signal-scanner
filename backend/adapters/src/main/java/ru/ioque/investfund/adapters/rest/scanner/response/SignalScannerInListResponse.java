package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.psql.entity.scanner.ScannerEntity;
import ru.ioque.investfund.domain.scanner.entity.ScannerStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
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
    ScannerStatus status;
    String description;
    LocalDateTime lastExecutionDateTime;

    public static SignalScannerInListResponse from(ScannerEntity scanner) {
        return SignalScannerInListResponse.builder()
            .id(scanner.getId())
            .status(scanner.getStatus())
            .description(scanner.getDescription())
            .lastExecutionDateTime(scanner.getLastExecutionDateTime())
            .build();
    }
}
