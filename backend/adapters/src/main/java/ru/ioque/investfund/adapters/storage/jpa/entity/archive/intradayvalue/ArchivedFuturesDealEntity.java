package ru.ioque.investfund.adapters.storage.jpa.entity.archive.intradayvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.Contract;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("FuturesDealEntity")
public class ArchivedFuturesDealEntity extends
    ArchivedIntradayValueEntity {
    Integer qnt;

    @Builder
    public ArchivedFuturesDealEntity(
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Integer qnt
    ) {
        super(dateTime, ticker, price);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return Contract.builder()
            .dateTime(dateTime)
            .ticker(ticker)
            .price(price)
            .qnt(qnt)
            .build();
    }

    public static ArchivedIntradayValueEntity from(Contract contract) {
        return ArchivedFuturesDealEntity.builder()
            .dateTime(contract.getDateTime())
            .ticker(contract.getTicker())
            .price(contract.getPrice())
            .qnt(contract.getQnt())
            .build();
    }
}
