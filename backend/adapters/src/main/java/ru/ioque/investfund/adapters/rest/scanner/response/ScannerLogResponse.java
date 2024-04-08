package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerLogEntity;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScannerLogResponse implements Serializable {
    String dateTime;
    String message;

    public static ScannerLogResponse from(ScannerLogEntity scannerLogEntity) {
        return new ScannerLogResponse(scannerLogEntity.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), scannerLogEntity.getMessage());
    }
}
