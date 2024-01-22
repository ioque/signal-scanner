package ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.tradingData.FuturesDeal;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("FuturesDealEntity")
public class FuturesDealEntity extends IntradayValueEntity {
    Integer qnt;

    @Builder
    public FuturesDealEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Integer qnt
    ) {
        super(number, dateTime, ticker, price);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return FuturesDeal.builder()
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .price(price)
            .qnt(qnt)
            .build();
    }

    public static IntradayValueEntity from(FuturesDeal futuresDeal) {
        return FuturesDealEntity.builder()
            .number(futuresDeal.getNumber())
            .dateTime(futuresDeal.getDateTime())
            .ticker(futuresDeal.getTicker())
            .price(futuresDeal.getPrice())
            .qnt(futuresDeal.getQnt())
            .build();
    }
}
