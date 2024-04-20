package ru.ioque.investfund.adapters.persistence.entity.archive.intradayvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("DealEntity")
public class ArchivedDeal extends ArchivedIntradayValue {
    Boolean isBuy;
    Integer qnt;

    @Builder
    public ArchivedDeal(
        Long id,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Boolean isBuy,
        Integer qnt,
        Double value
    ) {
        super(id, number, dateTime, ticker, price, value);
        this.isBuy = isBuy;
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return Deal.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .dateTime(dateTime)
            .price(price)
            .isBuy(isBuy)
            .qnt(qnt)
            .value(value)
            .build();
    }

    public static ArchivedIntradayValue from(Deal deal) {
        return ArchivedDeal.builder()
            .ticker(deal.getTicker().getValue())
            .number(deal.getNumber())
            .dateTime(deal.getDateTime())
            .price(deal.getPrice())
            .isBuy(deal.getIsBuy())
            .qnt(deal.getQnt())
            .value(deal.getValue())
            .build();
    }
}
