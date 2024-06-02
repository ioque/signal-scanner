package ru.ioque.investfund.adapters.psql.entity.datasource.intradayvalue;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "intraday_data")
@Entity(name = "IntradayData")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "INTRADAY_DATA_TYPE", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class IntradayDataEntity {

    @EmbeddedId
    IntradayPk id;

    @Column(nullable = false)
    String ticker;

    @Column(nullable = false)
    Instant timestamp;

    @Column(nullable = false)
    Double price;

    @Column(nullable = false)
    Double value;

    public IntradayDataEntity(
        Long number,
        UUID instrumentId,
        String ticker,
        Instant timestamp,
        Double price,
        Double value
    ) {
        this.id = new IntradayPk(number, instrumentId);
        this.ticker = ticker;
        this.timestamp = timestamp;
        this.price = price;
        this.value = value;
    }

    public abstract IntradayData toDomain();

    public static IntradayDataEntity fromDomain(IntradayData intradayData) {
        return mappers.get(intradayData.getClass()).apply(intradayData);
    }

    private static Map<Class<? extends IntradayData>, Function<IntradayData, IntradayDataEntity>> mappers = Map.of(
        Deal.class, domain -> DealEntity.from((Deal) domain),
        Contract.class, domain -> ContractEntity.from((Contract) domain),
        Delta.class, domain -> DeltaEntity.from((Delta) domain)
    );

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntradayDataEntity that = (IntradayDataEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

