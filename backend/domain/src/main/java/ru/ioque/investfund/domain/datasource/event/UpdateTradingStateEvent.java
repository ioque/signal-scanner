package ru.ioque.investfund.domain.datasource.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainEvent;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.TradingState;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UpdateTradingStateEvent implements DomainEvent {
    InstrumentId instrumentId;
    TradingState tradingState;

    public static DomainEvent of(InstrumentId id, TradingState tradingState) {
        return new UpdateTradingStateEvent(id, tradingState);
    }
}
