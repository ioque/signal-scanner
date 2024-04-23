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
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.ScannerEntity;
import ru.ioque.investfund.adapters.rest.datasource.response.InstrumentInListResponse;

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
    List<SignalResponse> signals;

    public static SignalScannerResponse of(
        ScannerEntity scanner,
        List<InstrumentEntity> instruments
    ) {
        return SignalScannerResponse.builder()
            .id(scanner.getId())
            .workPeriodInMinutes(scanner.getWorkPeriodInMinutes())
            .description(scanner.getDescription())
            .lastExecutionDateTime(scanner.getLastExecutionDateTime())
            .config(SignalConfigResponse.from(scanner))
            .instruments(instruments.stream().map(InstrumentInListResponse::from).toList())
            .signals(scanner.getSignals()
                .stream()
                .map(signal -> SignalResponse
                    .from(
                        signal,
                        instruments
                            .stream()
                            .filter(instrument -> instrument.getTicker().equals(signal.getId().getTicker()))
                            .findFirst()
                            .orElseThrow()
                    )
                )
                .toList())
            .build();
    }
}
