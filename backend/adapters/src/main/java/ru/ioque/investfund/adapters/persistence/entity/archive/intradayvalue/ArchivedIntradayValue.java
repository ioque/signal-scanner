package ru.ioque.investfund.adapters.persistence.entity.archive.intradayvalue;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.adapters.persistence.entity.GeneratedIdentity;
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "archived_intraday_value", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"number", "ticker"})})
@Entity(name = "ArchivedIntradayValue")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="INTRADAY_VALUE_TYPE", discriminatorType= DiscriminatorType.STRING, columnDefinition = "varchar(255)")
public abstract class ArchivedIntradayValue extends GeneratedIdentity {
    @Column(nullable = false)
    Long number;
    @Column(nullable = false)
    LocalDateTime dateTime;
    @Column(nullable = false)
    String ticker;
    @Column(nullable = false)
    Double price;
    @Column(nullable = false)
    Double value;

    public ArchivedIntradayValue(
        Long id,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        super(id);
        this.number = number;
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.price = price;
        this.value = value;
    }

    public abstract IntradayValue toDomain();

    public static ArchivedIntradayValue fromDomain(IntradayValue intradayValue) {
        return mappers.get(intradayValue.getClass()).apply(intradayValue);
    }

    private static Map<Class<? extends IntradayValue>, Function<IntradayValue, ArchivedIntradayValue>> mappers = Map.of(
        Deal.class, domain -> ArchivedDeal.from((Deal) domain),
        Contract.class, domain -> ArchivedFuturesDeal.from((Contract) domain),
        Delta.class, domain -> ArchivedIndexDelta.from((Delta) domain)
    );
}

