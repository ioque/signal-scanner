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
import ru.ioque.investfund.domain.datasource.value.Contract;
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
@DiscriminatorValue("FuturesDealEntity")
public class ArchivedFuturesDealEntity extends ArchivedIntradayValueEntity {
    Integer qnt;

    @Builder
    public ArchivedFuturesDealEntity(
        Long id,
        UUID datasourceId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(id, datasourceId, number, dateTime, ticker, price, value);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return Contract.builder()
            .datasourceId(DatasourceId.from(datasourceId))
            .instrumentId(InstrumentId.from(Ticker.from(ticker)))
            .number(number)
            .dateTime(dateTime)
            .price(price)
            .value(value)
            .qnt(qnt)
            .build();
    }

    public static ArchivedIntradayValueEntity from(Contract contract) {
        return ArchivedFuturesDealEntity.builder()
            .datasourceId(contract.getDatasourceId().getUuid())
            .ticker(contract.getInstrumentId().getTicker().getValue())
            .number(contract.getNumber())
            .dateTime(contract.getDateTime())
            .price(contract.getPrice())
            .value(contract.getValue())
            .qnt(contract.getQnt())
            .build();
    }
}
