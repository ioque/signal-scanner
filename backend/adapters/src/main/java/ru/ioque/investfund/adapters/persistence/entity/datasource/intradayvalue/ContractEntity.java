package ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("ContractEntity")
public class ContractEntity extends IntradayValueEntity {
    Integer qnt;

    @Builder
    public ContractEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(number, dateTime, ticker, price, value);
        this.qnt = qnt;
    }

    @Override
    public IntradayData toDomain() {
        return Contract.builder()
            .ticker(Ticker.from(getId().getTicker()))
            .number(getId().getNumber())
            .price(price)
            .value(value)
            .qnt(qnt)
            .build();
    }

    public static IntradayValueEntity from(Contract contract) {
        return ContractEntity.builder()
            .ticker(contract.getTicker().getValue())
            .number(contract.getNumber())
            .dateTime(contract.getTimestamp())
            .price(contract.getPrice())
            .value(contract.getValue())
            .qnt(contract.getQnt())
            .build();
    }
}
