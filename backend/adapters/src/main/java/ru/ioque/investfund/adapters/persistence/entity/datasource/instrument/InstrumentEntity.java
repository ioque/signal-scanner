package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.adapters.persistence.entity.UuidIdentity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.AggregatedHistoryEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.details.InstrumentDetailsEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.tradingstate.TradingStateEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "instrument")
@Entity(name = "Instrument")
public class InstrumentEntity extends UuidIdentity {
    @ManyToOne
    @JoinColumn(name = "datasource_id")
    DatasourceEntity datasource;

    @OneToOne(mappedBy = "instrument", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    TradingStateEntity tradingState;

    @OneToOne(mappedBy = "instrument", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    InstrumentDetailsEntity details;

    @Column(nullable = false)
    String ticker;

    Boolean updatable;

    @Enumerated(EnumType.STRING)
    InstrumentType type;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "instrument", fetch = FetchType.LAZY)
    List<AggregatedHistoryEntity> history;

    @Builder
    public InstrumentEntity(
        UUID id,
        String ticker,
        Boolean updatable,
        InstrumentType type,
        DatasourceEntity datasource,
        TradingStateEntity tradingState,
        InstrumentDetailsEntity details,
        List<AggregatedHistoryEntity> history
    ) {
        super(id);
        this.ticker = ticker;
        this.type = type;
        this.history = history;
        this.updatable = updatable;
        this.datasource = datasource;
        this.tradingState = tradingState;
        this.details = details;
    }

    public Optional<TradingStateEntity> getTradingState() {
        return Optional.ofNullable(tradingState);
    }

    public Instrument toDomain() {
        return Instrument.builder()
            .id(InstrumentId.from(getId()))
            .updatable(getUpdatable())
            .type(getType())
            .ticker(Ticker.from(getTicker()))
            .details(getDetails().toDomain())
            .tradingState(getTradingState().map(TradingStateEntity::toTradingState).orElse(null))
            .aggregateHistories(new TreeSet<>(
                history.stream().map(AggregatedHistoryEntity::toDomain).toList()
            ))
            .build();
    }

    public static InstrumentEntity fromDomain(Instrument domain) {
        InstrumentEntity instrumentEntity = InstrumentEntity.builder()
            .id(domain.getId().getUuid())
            .ticker(domain.getTicker().getValue())
            .updatable(domain.getUpdatable())
            .type(domain.getType())
            .build();
        instrumentEntity
            .setDetails(
                InstrumentDetailsEntity.of(instrumentEntity, domain.getDetails())
            );
        instrumentEntity
            .setTradingState(
                domain.getTradingState()
                    .map(tradingState -> TradingStateEntity.of(instrumentEntity, tradingState))
                    .orElse(null)
            );
        instrumentEntity
            .setHistory(
                domain
                    .getAggregateHistories()
                    .stream()
                    .map(row -> AggregatedHistoryEntity.fromDomain(instrumentEntity, row))
                    .toList()
            );
        return instrumentEntity;
    }
}
