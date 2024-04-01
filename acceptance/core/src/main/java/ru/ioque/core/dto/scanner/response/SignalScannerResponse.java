package ru.ioque.core.dto.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.core.dto.exchange.response.InstrumentInListResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalScannerResponse {
    UUID id;
    String description;
    Integer workPeriodInMinutes;
    LocalDateTime lastExecutionDateTime;
    SignalConfigResponse config;
    List<InstrumentInListResponse> instruments;
    List<ScannerLogResponse> logs;
    List<SignalResponse> signals;
}
