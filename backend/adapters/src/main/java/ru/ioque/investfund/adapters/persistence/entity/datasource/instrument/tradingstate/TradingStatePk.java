package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.tradingstate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TradingStatePk {
    private UUID instrumentId;

    public static TradingStatePk from(UUID instrumentId) {
        return new TradingStatePk(instrumentId);
    }
}
