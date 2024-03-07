package ru.ioque.investfund.adapters.rest.signalscanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalScannerEntity;

import java.io.Serializable;
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

    public static SignalScannerInListResponse from(SignalScannerEntity scanner) {
        return SignalScannerInListResponse.builder()
            .id(scanner.getId())
            .description(scanner.getDescription())
            .build();
    }
}