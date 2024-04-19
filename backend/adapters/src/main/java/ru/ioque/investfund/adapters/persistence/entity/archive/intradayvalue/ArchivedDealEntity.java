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
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.Deal;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.Ticker;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("DealEntity")
public class ArchivedDealEntity extends ArchivedIntradayValueEntity {
    Boolean isBuy;
    Integer qnt;

    @Builder
    public ArchivedDealEntity(
        Long id,
        UUID datasourceId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Boolean isBuy,
        Integer qnt,
        Double value
    ) {
        super(id, datasourceId, number, dateTime, ticker, price, value);
        this.isBuy = isBuy;
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return Deal.builder()
            .datasourceId(DatasourceId.from(datasourceId))
            .instrumentId(InstrumentId.from(Ticker.from(ticker)))
            .number(number)
            .dateTime(dateTime)
            .price(price)
            .isBuy(isBuy)
            .qnt(qnt)
            .value(value)
            .build();
    }

    public static ArchivedIntradayValueEntity from(Deal deal) {
        return ArchivedDealEntity.builder()
            .datasourceId(deal.getDatasourceId().getUuid())
            .ticker(deal.getInstrumentId().getTicker().getValue())
            .number(deal.getNumber())
            .dateTime(deal.getDateTime())
            .price(deal.getPrice())
            .isBuy(deal.getIsBuy())
            .qnt(deal.getQnt())
            .value(deal.getValue())
            .build();
    }
}
