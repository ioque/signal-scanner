package ru.ioque.investfund.adapters.storage.jpa.entity.archive.intradayvalue;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.domain.exchange.value.Deal;
import ru.ioque.investfund.domain.exchange.value.Contract;
import ru.ioque.investfund.domain.exchange.value.Delta;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "archived_intraday_value")
@Entity(name = "ArchivedIntradayValue")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="INTRADAY_VALUE_TYPE", discriminatorType= DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class ArchivedIntradayValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    Long number;
    @Column(nullable = false)
    LocalDateTime dateTime;
    @Column(nullable = false)
    String ticker;
    @Column(nullable = false)
    Double price;

    public ArchivedIntradayValueEntity(
        LocalDateTime dateTime,
        String ticker,
        Double price
    ) {
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.price = price;
    }

    public abstract IntradayValue toDomain();

    public static ArchivedIntradayValueEntity fromDomain(IntradayValue intradayValue) {
        return mappers.get(intradayValue.getClass()).apply(intradayValue);
    }

    private static Map<Class<? extends IntradayValue>, Function<IntradayValue, ArchivedIntradayValueEntity>> mappers = Map.of(
        Deal.class, domain -> ArchivedDealEntity.from((Deal) domain),
        Contract.class, domain -> ArchivedFuturesDealEntity.from((Contract) domain),
        Delta.class, domain -> ArchivedIndexDeltaEntity.from((Delta) domain)
    );
}

