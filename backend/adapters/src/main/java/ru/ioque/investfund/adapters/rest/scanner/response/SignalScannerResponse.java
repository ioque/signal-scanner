package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.rest.exchange.response.InstrumentInListResponse;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerLogEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ScannerEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalScannerResponse implements Serializable {
    UUID id;
    String description;
    Integer workPeriodInMinutes;
    LocalDateTime lastExecutionDateTime;
    SignalConfigResponse config;
    List<InstrumentInListResponse> instruments;
    List<ScannerLogResponse> logs;
    List<SignalResponse> signals;

    public static SignalScannerResponse from(
        ScannerEntity scanner,
        List<InstrumentEntity> instruments,
        List<ScannerLogEntity> logs
    ) {
        return SignalScannerResponse.builder()
            .id(scanner.getId())
            .workPeriodInMinutes(scanner.getWorkPeriodInMinutes())
            .description(scanner.getDescription())
            .lastExecutionDateTime(scanner.getLastExecutionDateTime())
            .config(SignalConfigResponse.from(scanner))
            .instruments(instruments.stream().map(InstrumentInListResponse::from).toList())
            .logs(logs.stream().map(ScannerLogResponse::from).toList())
            .signals(scanner.getSignals()
                .stream()
                .map(signal -> SignalResponse
                    .from(
                        signal,
                        instruments
                            .stream()
                            .filter(instrument -> instrument.getTicker().equals(signal.getTicker()))
                            .findFirst()
                            .orElseThrow()
                    )
                )
                .toList())
            .build();
    }
}
