package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.AggregatedHistoryEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetails;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("CurrencyPairEntity")
public class CurrencyPairEntity extends InstrumentEntity {
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPairEntity(
        UUID id,
        DatasourceEntity datasource,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        TradingStateEmbeddable tradingState,
        List<AggregatedHistoryEntity> history,
        Integer lotSize,
        String faceUnit
    ) {
        super(id, datasource, ticker, shortName, name, updatable, tradingState, history);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    @Override
    public Instrument toDomain() {
        return Instrument.builder()
            .id(InstrumentId.from(getId()))
            .details(
                CurrencyPairDetails.builder()
                    .ticker(Ticker.from(this.getTicker()))
                    .name(this.getName())
                    .shortName(this.getShortName())
                    .lotSize(this.getLotSize())
                    .faceUnit(this.getFaceUnit())
                    .build()
            )
            .updatable(this.getUpdatable())
            .tradingState(getTradingState().map(TradingStateEmbeddable::toTradingState).orElse(null))
            .aggregateHistories(new TreeSet<>(
                history.stream().map(AggregatedHistoryEntity::toDomain).toList()
            ))
            .build();
    }
}
