package ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.intradayvalue;

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
import ru.ioque.investfund.domain.exchange.value.tradingData.Deal;
import ru.ioque.investfund.domain.exchange.value.tradingData.FuturesDeal;
import ru.ioque.investfund.domain.exchange.value.tradingData.IndexDelta;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

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
    LocalDateTime dateTime;
    @Column(nullable = false)
    Long number;
    @Column(nullable = false)
    String ticker;
    @Column(nullable = false)
    Double price;

    public ArchivedIntradayValueEntity(
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

    public static ArchivedIntradayValueEntity fromDomain(IntradayValue intradayValue) {
        return mappers.get(intradayValue.getClass()).apply(intradayValue);
    }

    private static Map<Class<? extends IntradayValue>, Function<IntradayValue, ArchivedIntradayValueEntity>> mappers = Map.of(
        Deal.class, domain -> ArchivedDealEntity.from((Deal) domain),
        FuturesDeal.class, domain -> ArchivedFuturesDealEntity.from((FuturesDeal) domain),
        IndexDelta.class, domain -> ArchivedIndexDeltaEntity.from((IndexDelta) domain)
    );
}
