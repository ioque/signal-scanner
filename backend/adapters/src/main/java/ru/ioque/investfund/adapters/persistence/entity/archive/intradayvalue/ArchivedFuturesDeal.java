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
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("FuturesDealEntity")
public class ArchivedFuturesDeal extends ArchivedIntradayValue {
    Integer qnt;

    @Builder
    public ArchivedFuturesDeal(
        Long id,
        UUID datasourceId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(id, number, dateTime, ticker, price, value);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return Contract.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .dateTime(dateTime)
            .price(price)
            .value(value)
            .qnt(qnt)
            .build();
    }

    public static ArchivedIntradayValue from(Contract contract) {
        return ArchivedFuturesDeal.builder()
            .ticker(contract.getTicker().getValue())
            .number(contract.getNumber())
            .dateTime(contract.getDateTime())
            .price(contract.getPrice())
            .value(contract.getValue())
            .qnt(contract.getQnt())
            .build();
    }
}
