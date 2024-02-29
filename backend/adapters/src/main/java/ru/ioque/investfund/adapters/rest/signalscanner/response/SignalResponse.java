package ru.ioque.investfund.adapters.rest.signalscanner.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalEntity;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SignalResponse {
    UUID instrumentId;
    boolean isBuy;
    public static SignalResponse from(SignalEntity signalEntity) {
        return SignalResponse.builder()
            .instrumentId(signalEntity.getInstrumentId())
            .isBuy(signalEntity.isBuy())
            .build();
    }
}
