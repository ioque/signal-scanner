package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.adapters.persistence.entity.UuidIdentity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.AggregatedHistoryEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetails;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetails;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetails;
import ru.ioque.investfund.domain.datasource.value.details.StockDetails;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "instrument", uniqueConstraints = { @UniqueConstraint(columnNames = { "datasource_id", "ticker" }) })
@Entity(name = "Instrument")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="INSTRUMENT_TYPE", discriminatorType= DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class InstrumentEntity extends UuidIdentity {
    @ManyToOne
    @JoinColumn(name = "datasource_id")
    DatasourceEntity datasource;
    @Column(nullable = false)
    String ticker;
    @Column(nullable = false)
    String shortName;
    @Column(nullable = false)
    String name;
    Boolean updatable;
    @Embedded
    TradingStateEmbeddable tradingState;
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "instrument", fetch = FetchType.LAZY)
    List<AggregatedHistoryEntity> history;

    public InstrumentEntity(
        UUID id,
        DatasourceEntity datasource,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        TradingStateEmbeddable tradingState,
        List<AggregatedHistoryEntity> history
    ) {
        super(id);
        this.datasource = datasource;
        this.ticker = ticker;
        this.shortName = shortName;
        this.name = name;
        this.updatable = updatable;
        this.tradingState = tradingState;
        this.history = history;
    }

    public abstract Instrument toDomain();

    public Optional<TradingStateEmbeddable> getTradingState() {
        return Optional.ofNullable(tradingState);
    }

    public static InstrumentEntity toEntityFrom(Instrument instrument) {
        return instrumentMappers.get(instrument.getType()).apply(instrument);
    }

    private static Map<InstrumentType, Function<Instrument, InstrumentEntity>> instrumentMappers = Map.of(
        InstrumentType.FUTURES, InstrumentEntity::toFuturesEntityFrom,
        InstrumentType.STOCK, InstrumentEntity::toStockEntityFrom,
        InstrumentType.CURRENCY_PAIR, InstrumentEntity::toCurrencyPairEntityFrom,
        InstrumentType.INDEX, InstrumentEntity::toIndexEntityFrom
    );

    public static InstrumentEntity toStockEntityFrom(Instrument domain) {
        StockDetails details = (StockDetails) domain.getDetails();
        StockEntity stockEntity = StockEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .tradingState(domain.getTradingState().map(TradingStateEmbeddable::from).orElse(null))
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .shortName(details.getShortName())
            .lotSize(details.getLotSize())
            .isin(details.getIsin().getValue())
            .regNumber(details.getRegNumber())
            .listLevel(details.getListLevel())
            .build();
        List<AggregatedHistoryEntity> history = domain
            .getAggregateHistories()
            .stream()
            .map(AggregatedHistoryEntity::fromDomain)
            .peek(row -> row.setInstrument(stockEntity))
            .toList();
        stockEntity.setHistory(history);
        return stockEntity;
    }

    public static InstrumentEntity toIndexEntityFrom(Instrument domain) {
        IndexDetails details = (IndexDetails) domain.getDetails();
        IndexEntity indexEntity = IndexEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .tradingState(domain.getTradingState().map(TradingStateEmbeddable::from).orElse(null))
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .shortName(details.getShortName())
            .annualHigh(details.getAnnualHigh())
            .annualLow(details.getAnnualLow())
            .build();
        List<AggregatedHistoryEntity> history = domain
            .getAggregateHistories()
            .stream()
            .map(AggregatedHistoryEntity::fromDomain)
            .peek(row -> row.setInstrument(indexEntity))
            .toList();
        indexEntity.setHistory(history);
        return indexEntity;
    }

    public static InstrumentEntity toFuturesEntityFrom(Instrument domain) {
        FuturesDetails details = (FuturesDetails) domain.getDetails();
        FuturesEntity futuresEntity = FuturesEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .tradingState(domain.getTradingState().map(TradingStateEmbeddable::from).orElse(null))
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .shortName(details.getShortName())
            .lotVolume(details.getLotVolume())
            .initialMargin(details.getInitialMargin())
            .highLimit(details.getHighLimit())
            .lowLimit(details.getLowLimit())
            .assetCode(details.getAssetCode())
            .build();
        List<AggregatedHistoryEntity> history = domain
            .getAggregateHistories()
            .stream()
            .map(AggregatedHistoryEntity::fromDomain)
            .peek(row -> row.setInstrument(futuresEntity))
            .toList();
        futuresEntity.setHistory(history);
        return futuresEntity;
    }

    public static InstrumentEntity toCurrencyPairEntityFrom(Instrument domain) {
        CurrencyPairDetails details = (CurrencyPairDetails) domain.getDetails();
        CurrencyPairEntity currencyPairEntity = CurrencyPairEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .tradingState(domain.getTradingState().map(TradingStateEmbeddable::from).orElse(null))
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .shortName(details.getShortName())
            .lotSize(details.getLotSize())
            .faceUnit(details.getFaceUnit())
            .build();
        List<AggregatedHistoryEntity> history = domain
            .getAggregateHistories()
            .stream()
            .map(AggregatedHistoryEntity::fromDomain)
            .peek(row -> row.setInstrument(currencyPairEntity))
            .toList();
        currencyPairEntity.setHistory(history);
        return currencyPairEntity;
    }
}
