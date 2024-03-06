package ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue;

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
import ru.ioque.investfund.domain.exchange.value.FuturesDeal;
import ru.ioque.investfund.domain.exchange.value.IndexDelta;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "intraday_value")
@Entity(name = "IntradayValue")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="INTRADAY_VALUE_TYPE", discriminatorType= DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class IntradayValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    LocalDateTime dateTime;
    @Column(nullable = false)
    Long number;
    @Column(nullable = false)
    String ticker;
    @Column(nullable = false)
    Double price;

    public IntradayValueEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price
    ) {
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.price = price;
        this.number = number;
    }

    public abstract IntradayValue toDomain();

    public static IntradayValueEntity fromDomain(IntradayValue intradayValue) {
        return mappers.get(intradayValue.getClass()).apply(intradayValue);
    }

    private static Map<Class<? extends IntradayValue>, Function<IntradayValue, IntradayValueEntity>> mappers = Map.of(
        Deal.class, domain -> DealEntity.from((Deal) domain),
        FuturesDeal.class, domain -> FuturesDealEntity.from((FuturesDeal) domain),
        IndexDelta.class, domain -> IndexDeltaEntity.from((IndexDelta) domain)
    );
}

