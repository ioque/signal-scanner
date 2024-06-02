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
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("ContractEntity")
public class ContractEntity extends IntradayDataEntity {

    Integer qnt;

    @Builder
    public ContractEntity(
        Long number,
        UUID instrumentId,
        String ticker,
        Instant timestamp,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(number, instrumentId, ticker, timestamp, price, value);
        this.qnt = qnt;
    }

    @Override
    public IntradayData toDomain() {
        return Contract.builder()
            .instrumentId(InstrumentId.from(getId().getInstrumentId()))
            .ticker(Ticker.from(getTicker()))
            .number(getId().getNumber())
            .price(price)
            .value(value)
            .qnt(qnt)
            .timestamp(getTimestamp())
            .build();
    }

    public static IntradayDataEntity from(Contract contract) {
        return ContractEntity.builder()
            .instrumentId(contract.getInstrumentId().getUuid())
            .ticker(contract.getTicker().getValue())
            .number(contract.getNumber())
            .timestamp(contract.getTimestamp())
            .price(contract.getPrice())
            .value(contract.getValue())
            .qnt(contract.getQnt())
            .build();
    }
}
