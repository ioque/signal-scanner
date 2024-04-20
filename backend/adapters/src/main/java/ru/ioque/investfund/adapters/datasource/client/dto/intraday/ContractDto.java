package ru.ioque.investfund.adapters.datasource.client.dto.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractDto extends IntradayValueDto {
    Integer qnt;

    @Builder
    public ContractDto(Long number, LocalDateTime dateTime, String ticker, Double value, Double price, Integer qnt) {
        super(number, dateTime, ticker, value, price);
        this.qnt = qnt;
    }

    @Override
    public IntradayValue toIntradayValue() {
        return Contract.builder()
            .ticker(Ticker.from(getTicker()))
            .number(getNumber())
            .dateTime(getDateTime())
            .value(getValue())
            .price(getPrice())
            .qnt(qnt)
            .build();
    }
}
