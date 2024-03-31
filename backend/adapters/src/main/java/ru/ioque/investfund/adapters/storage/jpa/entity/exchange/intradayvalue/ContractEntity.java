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
import ru.ioque.investfund.domain.exchange.value.Contract;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

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
        Integer qnt
    ) {
        super(number, dateTime, ticker, price);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return Contract.builder()
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .price(price)
            .qnt(qnt)
            .build();
    }

    public static IntradayValueEntity from(Contract contract) {
        return ContractEntity.builder()
            .number(contract.getNumber())
            .dateTime(contract.getDateTime())
            .ticker(contract.getTicker())
            .price(contract.getPrice())
            .qnt(contract.getQnt())
            .build();
    }
}
