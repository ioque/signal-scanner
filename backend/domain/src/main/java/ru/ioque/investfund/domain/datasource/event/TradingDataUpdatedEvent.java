package ru.ioque.investfund.domain.datasource.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TradingDataUpdatedEvent extends DomainEvent {
    private final UUID datasourceId;

    @Builder
    public TradingDataUpdatedEvent(UUID id, LocalDateTime dateTime, UUID datasourceId) {
        super(id, dateTime);
        this.datasourceId = datasourceId;
    }
}
