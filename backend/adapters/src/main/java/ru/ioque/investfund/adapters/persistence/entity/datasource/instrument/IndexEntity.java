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
import ru.ioque.investfund.domain.datasource.value.details.IndexDetails;
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
@DiscriminatorValue("IndexEntity")
public class IndexEntity extends InstrumentEntity {
    Double annualHigh;
    Double annualLow;

    @Builder
    public IndexEntity(
        UUID id,
        DatasourceEntity datasource,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        TradingStateEmbeddable tradingState,
        List<AggregatedHistoryEntity> history,
        Double annualHigh,
        Double annualLow
    ) {
        super(id, datasource, ticker, shortName, name, updatable, tradingState, history);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }

    @Override
    public Instrument toDomain() {
        return Instrument.builder()
            .id(InstrumentId.from(getId()))
            .details(
                IndexDetails.builder()
                    .ticker(Ticker.from(this.getTicker()))
                    .name(this.getName())
                    .shortName(this.getShortName())
                    .annualHigh(this.getAnnualHigh())
                    .annualLow(this.getAnnualLow())
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
