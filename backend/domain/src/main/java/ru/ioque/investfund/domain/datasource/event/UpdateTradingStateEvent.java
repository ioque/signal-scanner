package ru.ioque.investfund.domain.datasource.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainEvent;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.TradingState;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTradingStateEvent implements DomainEvent {
    InstrumentId instrumentId;
    TradingState tradingState;

    public static DomainEvent of(InstrumentId id, TradingState tradingState) {
        return new UpdateTradingStateEvent(id, tradingState);
    }
}
