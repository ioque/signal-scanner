package ru.ioque.investfund.application.integration.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalRegisteredEvent implements IntegrationEvent {
    UUID id;
    String ticker;
    Double price;
    boolean isBuy;
    UUID scannerId;
    LocalDateTime watermark;

    public static SignalRegisteredEvent of(UUID id, ScannerId scannerId, Signal newSignal) {
        return SignalRegisteredEvent.builder()
            .id(id)
            .ticker(newSignal.getTicker().getValue())
            .price(newSignal.getPrice())
            .isBuy(newSignal.isBuy())
            .scannerId(scannerId.getUuid())
            .watermark(newSignal.getWatermark())
            .build();
    }
}
