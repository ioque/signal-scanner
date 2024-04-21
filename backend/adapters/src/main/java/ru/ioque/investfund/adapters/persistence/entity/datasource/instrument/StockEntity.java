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
import ru.ioque.investfund.domain.datasource.value.details.StockDetails;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
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
@DiscriminatorValue("StockEntity")
public class StockEntity extends InstrumentEntity {
    Integer lotSize;
    String isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public StockEntity(
        UUID id,
        DatasourceEntity datasource,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        TradingStateEmbeddable tradingState,
        List<AggregatedHistoryEntity> history,
        Integer lotSize,
        String isin,
        String regNumber,
        Integer listLevel
    ) {
        super(id, datasource, ticker, shortName, name, updatable, tradingState, history);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }

    @Override
    public Instrument toDomain() {
        return Instrument.builder()
            .id(InstrumentId.from(getId()))
            .details(
                StockDetails.builder()
                    .ticker(Ticker.from(this.getTicker()))
                    .name(this.getName())
                    .shortName(this.getShortName())
                    .lotSize(this.getLotSize())
                    .isin(Isin.from(this.getIsin()))
                    .regNumber(this.getRegNumber())
                    .listLevel(this.getListLevel())
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
