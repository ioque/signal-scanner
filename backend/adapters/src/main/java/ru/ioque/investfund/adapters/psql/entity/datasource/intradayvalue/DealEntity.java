package ru.ioque.investfund.adapters.psql.entity.datasource.intradayvalue;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("DealEntity")
public class DealEntity extends IntradayDataEntity {

    Boolean isBuy;

    Integer qnt;

    @Builder
    public DealEntity(
        Long number,
        UUID instrumentId,
        String ticker,
        Instant dateTime,
        Double price,
        Boolean isBuy,
        Integer qnt,
        Double value
    ) {
        super(number, instrumentId, ticker, dateTime, price, value);
        this.isBuy = isBuy;
        this.qnt = qnt;
    }

    @Override
    public IntradayData toDomain() {
        return Deal.builder()
            .instrumentId(InstrumentId.from(getId().getInstrumentId()))
            .ticker(Ticker.from(getTicker()))
            .number(getId().getNumber())
            .price(price)
            .isBuy(isBuy)
            .qnt(qnt)
            .value(value)
            .build();
    }

    public static IntradayDataEntity from(Deal deal) {
        return DealEntity.builder()
            .instrumentId(deal.getInstrumentId().getUuid())
            .ticker(deal.getTicker().getValue())
            .number(deal.getNumber())
            .price(deal.getPrice())
            .isBuy(deal.getIsBuy())
            .qnt(deal.getQnt())
            .value(deal.getValue())
            .build();
    }
}
