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
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradingDataIntegratedEvent implements IntegrationEvent {
    UUID id;
    UUID datasourceId;
    Integer updatedCount;
    LocalDateTime dateTime;

    @Override
    public boolean isTradingDataIntegratedEvent() {
        return true;
    }
}
