package ru.ioque.investfund.domain.datasource.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class TradingDataUpdatedEvent implements DomainEvent {
    private UUID id;
    private LocalDateTime dateTime;
    private UUID datasourceId;

    @Builder
    public TradingDataUpdatedEvent(UUID id, LocalDateTime dateTime, UUID datasourceId) {
        this.id = id;
        this.dateTime = dateTime;
        this.datasourceId = datasourceId;
    }
}
