package ru.ioque.investfund.adapters.rest.signalscanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerLogEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportLogResponse implements Serializable {
    LocalDateTime dateTime;
    String message;

    public static ReportLogResponse from(ScannerLogEntity scannerLogEntity) {
        return new ReportLogResponse(scannerLogEntity.getDateTime(), scannerLogEntity.getMessage());
    }
}
