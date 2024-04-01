package ru.ioque.moexdatasource.adapters.rest.response.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.intraday.Deal;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DealResponse extends IntradayValueResponse {
    Integer qnt;
    Boolean isBuy;

    @Builder
    public DealResponse(
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

    public static DealResponse from(Deal intradayValue) {
        return DealResponse.builder()
            .number(intradayValue.getNumber())
            .ticker(intradayValue.getTicker())
            .dateTime(intradayValue.getDateTime())
            .price(intradayValue.getPrice())
            .value(intradayValue.getValue())
            .isBuy(intradayValue.getIsBuy())
            .qnt(intradayValue.getQnt())
            .build();
    }
}
