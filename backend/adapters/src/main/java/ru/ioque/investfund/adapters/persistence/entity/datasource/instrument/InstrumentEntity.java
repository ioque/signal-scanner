package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.adapters.persistence.entity.UuidIdentity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetails;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetails;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetails;
import ru.ioque.investfund.domain.datasource.value.details.StockDetails;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.time.LocalDate;
import java.util.Map;
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
    LocalDate lastHistoryDate;
    Long lastTradingNumber;

    public InstrumentEntity(
        UUID id,
        DatasourceEntity datasource,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id);
        this.datasource = datasource;
        this.ticker = ticker;
        this.shortName = shortName;
        this.name = name;
        this.updatable = updatable;
        this.lastHistoryDate = lastHistoryDate;
        this.lastTradingNumber = lastTradingNumber;
    }

    public abstract Instrument toDomain();

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
        return StockEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .lastHistoryDate(domain.getLastHistoryDate().orElse(null))
            .lastTradingNumber(domain.getLastTradingNumber())
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .shortName(details.getShortName())
            .lotSize(details.getLotSize())
            .isin(details.getIsin().getValue())
            .regNumber(details.getRegNumber())
            .listLevel(details.getListLevel())
            .build();
    }

    public static InstrumentEntity toIndexEntityFrom(Instrument domain) {
        IndexDetails details = (IndexDetails) domain.getDetails();
        return IndexEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .lastHistoryDate(domain.getLastHistoryDate().orElse(null))
            .lastTradingNumber(domain.getLastTradingNumber())
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .shortName(details.getShortName())
            .annualHigh(details.getAnnualHigh())
            .annualLow(details.getAnnualLow())
            .build();
    }

    public static InstrumentEntity toFuturesEntityFrom(Instrument domain) {
        FuturesDetails details = (FuturesDetails) domain.getDetails();
        return FuturesEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .lastHistoryDate(domain.getLastHistoryDate().orElse(null))
            .lastTradingNumber(domain.getLastTradingNumber())
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .shortName(details.getShortName())
            .lotVolume(details.getLotVolume())
            .initialMargin(details.getInitialMargin())
            .highLimit(details.getHighLimit())
            .lowLimit(details.getLowLimit())
            .assetCode(details.getAssetCode())
            .build();
    }

    public static InstrumentEntity toCurrencyPairEntityFrom(Instrument domain) {
        CurrencyPairDetails details = (CurrencyPairDetails) domain.getDetails();
        return CurrencyPairEntity.builder()
            .id(domain.getId().getUuid())
            .updatable(domain.getUpdatable())
            .lastHistoryDate(domain.getLastHistoryDate().orElse(null))
            .lastTradingNumber(domain.getLastTradingNumber())
            .ticker(details.getTicker().getValue())
            .name(details.getName())
            .shortName(details.getShortName())
            .lotSize(details.getLotSize())
            .faceUnit(details.getFaceUnit())
            .build();
    }
}
