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
import ru.ioque.investfund.domain.datasource.value.Contract;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDateTime;
import java.util.UUID;

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
        UUID datasourceId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(datasourceId, number, dateTime, ticker, price, value);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return Contract.builder()
            .datasourceId(datasourceId)
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .price(price)
            .value(value)
            .qnt(qnt)
            .build();
    }

    public static IntradayValueEntity from(Contract contract) {
        return ContractEntity.builder()
            .datasourceId(contract.getDatasourceId())
            .number(contract.getNumber())
            .dateTime(contract.getDateTime())
            .ticker(contract.getTicker())
            .price(contract.getPrice())
            .value(contract.getValue())
            .qnt(contract.getQnt())
            .build();
    }
}