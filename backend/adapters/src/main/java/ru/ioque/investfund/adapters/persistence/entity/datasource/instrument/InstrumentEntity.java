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
import ru.ioque.investfund.adapters.persistence.entity.GeneratedIdEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "instrument", uniqueConstraints = { @UniqueConstraint(columnNames = { "datasource_id", "ticker" }) })
@Entity(name = "Instrument")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="INSTRUMENT_TYPE", discriminatorType= DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class InstrumentEntity extends GeneratedIdEntity {
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
        Long id,
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

    public static InstrumentEntity from(Instrument instrument) {
        return instrumentMappers.get(instrument.getClass()).apply(instrument);
    }

    private static Map<Class<? extends Instrument>, Function<Instrument, InstrumentEntity>> instrumentMappers = Map.of(
        Futures.class, domain -> FuturesEntity.from((Futures) domain),
        Stock.class, domain -> StockEntity.from((Stock) domain),
        CurrencyPair.class, domain -> CurrencyPairEntity.from((CurrencyPair) domain),
        Index.class, domain -> IndexEntity.from((Index) domain)
    );
}
