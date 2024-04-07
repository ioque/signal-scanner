package ru.ioque.investfund.adapters.exchagne.moex.client.dto.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.Deal;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealDto extends IntradayValueDto {
    Integer qnt;
    Boolean isBuy;

    @Builder
    public DealDto(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double value,
        Double price,
        Integer qnt,
        Boolean isBuy
    ) {
        super(number, dateTime, ticker, value, price);
        this.qnt = qnt;
        this.isBuy = isBuy;
    }

    @Override
    public IntradayValue toDomain() {
        return Deal.builder()
            .number(getNumber())
            .ticker(getTicker())
            .dateTime(getDateTime())
            .value(getValue())
            .price(getPrice())
            .qnt(qnt)
            .isBuy(isBuy)
            .build();
    }
}
