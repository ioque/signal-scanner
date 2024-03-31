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
    public ContractResponse(LocalDateTime dateTime, String ticker, Double value, Double price, Integer qnt) {
        super(dateTime, ticker, value, price);
        this.qnt = qnt;
    }

    public static ContractResponse from(Contract intradayValue) {
        return ContractResponse.builder()
            .ticker(intradayValue.getTicker())
            .dateTime(intradayValue.getDateTime())
            .price(intradayValue.getPrice())
            .value(intradayValue.getValue())
            .qnt(intradayValue.getQnt())
            .build();
    }
}
