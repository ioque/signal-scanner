package ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.intradayvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.tradingData.Deal;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("DealEntity")
public class ArchivedDealEntity extends
    ArchivedIntradayValueEntity {
    Boolean isBuy;
    Integer qnt;
    Double value;

    @Builder
    public ArchivedDealEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Boolean isBuy,
        Integer qnt,
        Double value
    ) {
        super(number, dateTime, ticker, price);
        this.isBuy = isBuy;
        this.qnt = qnt;
        this.value = value;
    }

    @Override
    public IntradayValue toDomain() {
        return Deal.builder()
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .price(price)
            .isBuy(isBuy)
            .qnt(qnt)
            .value(value)
            .build();
    }

    public static ArchivedIntradayValueEntity from(Deal deal) {
        return ArchivedDealEntity.builder()
            .number(deal.getNumber())
            .dateTime(deal.getDateTime())
            .ticker(deal.getTicker())
            .price(deal.getPrice())
            .isBuy(deal.getIsBuy())
            .qnt(deal.getQnt())
            .value(deal.getValue())
            .build();
    }
}
