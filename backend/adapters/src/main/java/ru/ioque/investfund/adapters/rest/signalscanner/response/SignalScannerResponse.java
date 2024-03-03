package ru.ioque.investfund.adapters.rest.signalscanner.response;

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
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.ReportEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalScannerEntity;

import java.io.Serializable;
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
    SignalConfigResponse config;
    List<InstrumentInListResponse> instruments;
    List<ReportResponse> reports;
    List<SignalResponse> signals;

    public static SignalScannerResponse from(
        SignalScannerEntity scanner,
        List<InstrumentEntity> instruments,
        List<ReportEntity> reports,
        List<SignalEntity> signals
    ) {
        return SignalScannerResponse.builder()
            .id(scanner.getId())
            .description(scanner.getDescription())
            .config(SignalConfigResponse.from(scanner))
            .instruments(instruments.stream().map(InstrumentInListResponse::from).toList())
            .reports(reports.stream().map(ReportResponse::from).toList())
            .signals(signals
                .stream()
                .map(signal -> SignalResponse
                    .from(
                        signal,
                        instruments
                            .stream()
                            .filter(instrument -> instrument.getId().equals(signal.getInstrumentId()))
                            .findFirst()
                            .orElseThrow()
                    )
                )
                .toList())
            .build();
    }
}
