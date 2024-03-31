package ru.ioque.moexdatasource.adapters.rest.response.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.intraday.Contract;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ContractResponse extends IntradayValueResponse {
    Integer qnt;

    @Builder
    public ContractResponse(Long tradeNumber, LocalDateTime dateTime, String ticker, Double value, Double price, Integer qnt) {
        super(tradeNumber, dateTime, ticker, value, price);
        this.qnt = qnt;
    }

    public static ContractResponse from(Contract intradayValue) {
        return ContractResponse.builder()
            .tradeNumber(intradayValue.getTradeNumber())
            .ticker(intradayValue.getTicker())
            .dateTime(intradayValue.getDateTime())
            .price(intradayValue.getPrice())
            .value(intradayValue.getValue())
            .qnt(intradayValue.getQnt())
            .build();
    }
}
