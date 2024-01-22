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
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalScannerResponse {
    UUID id;
    String businessProcessorName;

    public static SignalScannerResponse fromDomain(SignalScannerBot dataScanner) {
        return SignalScannerResponse.builder()
            .id(dataScanner.getId())
            .businessProcessorName(dataScanner.getConfig().factorySearchAlgorithm().getName())
            .build();
    }
}
