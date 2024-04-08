package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.adapters.persistence.entity.AbstractEntity;
import ru.ioque.investfund.domain.datasource.entity.CurrencyPair;
import ru.ioque.investfund.domain.datasource.entity.Futures;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.Stock;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "instrument")
@Entity(name = "Instrument")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="INSTRUMENT_TYPE", discriminatorType= DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class InstrumentEntity extends AbstractEntity {
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
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id);
        this.ticker = ticker;
        this.shortName = shortName;
        this.name = name;
        this.updatable = updatable;
        this.lastHistoryDate = lastHistoryDate;
        this.lastTradingNumber = lastTradingNumber;
    }

    public abstract Instrument toDomain();

    public static InstrumentEntity fromDomain(Instrument instrument) {
        return instrumentMappers.get(instrument.getClass()).apply(instrument);
    }

    private static Map<Class<? extends Instrument>, Function<Instrument, InstrumentEntity>> instrumentMappers = Map.of(
        Futures.class, domain -> FuturesEntity.from((Futures) domain),
        Stock.class, domain -> StockEntity.from((Stock) domain),
        CurrencyPair.class, domain -> CurrencyPairEntity.from((CurrencyPair) domain),
        Index.class, domain -> IndexEntity.from((Index) domain)
    );
}
