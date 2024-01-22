package ru.ioque.investfund.adapters.storage.jpa.entity.exchange.dailyvalue;

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
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.DealResult;
import ru.ioque.investfund.domain.exchange.value.tradingData.FuturesDealResult;
import ru.ioque.investfund.domain.exchange.value.tradingData.IndexDeltaResult;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity(name = "DailyValue")
@Table(name = "daily_value")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DAILY_VALUE_TYPE", discriminatorType= DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class DailyValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    LocalDate tradeDate;
    @Column(nullable = false)
    String ticker;
    @Column(nullable = false)
    Double openPrice;
    @Column(nullable = false)
    Double closePrice;
    @Column(nullable = false)
    Double minPrice;
    @Column(nullable = false)
    Double maxPrice;
    @Column(nullable = false)
    Double value;

    public DailyValueEntity(
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double value
    ) {
        this.tradeDate = tradeDate;
        this.ticker = ticker;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.value = value;
    }

    public abstract DailyValue toDomain();

    public static DailyValueEntity fromDomain(DailyValue dailyValue) {
        return mappers.get(dailyValue.getClass()).apply(dailyValue);
    }

    private static Map<Class<? extends DailyValue>, Function<DailyValue, DailyValueEntity>> mappers = Map.of(
        DealResult.class, domain -> DealResultEntity.from((DealResult) domain),
        FuturesDealResult.class, domain -> FuturesDealResultEntity.from((FuturesDealResult) domain),
        IndexDeltaResult.class, domain -> IndexDeltaResultEntity.from((IndexDeltaResult) domain)
    );
}
