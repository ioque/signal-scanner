package ru.ioque.apitest.kafka;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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

    @Override
    public boolean isSignalRegisteredEvent() {
        return true;
    }
}
