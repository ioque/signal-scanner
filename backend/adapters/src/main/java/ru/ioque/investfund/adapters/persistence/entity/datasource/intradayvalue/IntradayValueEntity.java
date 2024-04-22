package ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue;

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
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "intraday_value")
@Entity(name = "IntradayValue")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "INTRADAY_VALUE_TYPE", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class IntradayValueEntity {
    @EmbeddedId
    IntradayPk id;
    @Column(nullable = false)
    LocalDateTime dateTime;
    @Column(nullable = false)
    Double price;
    @Column(nullable = false)
    Double value;

    public IntradayValueEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        this.id = new IntradayPk(number, ticker);
        this.dateTime = dateTime;
        this.price = price;
        this.value = value;
    }

    public abstract IntradayData toDomain();

    public static IntradayValueEntity fromDomain(IntradayData intradayData) {
        return mappers.get(intradayData.getClass()).apply(intradayData);
    }

    private static Map<Class<? extends IntradayData>, Function<IntradayData, IntradayValueEntity>> mappers = Map.of(
        Deal.class, domain -> DealEntity.from((Deal) domain),
        Contract.class, domain -> ContractEntity.from((Contract) domain),
        Delta.class, domain -> DeltaEntity.from((Delta) domain)
    );

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntradayValueEntity that = (IntradayValueEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

