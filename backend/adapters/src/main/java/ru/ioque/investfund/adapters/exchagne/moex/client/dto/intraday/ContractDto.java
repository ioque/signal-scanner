package ru.ioque.investfund.adapters.exchagne.moex.client.dto.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.Contract;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractDto extends IntradayValueDto {
    Integer qnt;

    @Builder
    public ContractDto(LocalDateTime dateTime, String ticker, Double value, Double price, Integer qnt) {
        super(dateTime, ticker, value, price);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toDomain() {
        return Contract.builder()
            .ticker(getTicker())
            .dateTime(getDateTime())
            .value(getValue())
            .price(getPrice())
            .qnt(qnt)
            .build();
    }
}
