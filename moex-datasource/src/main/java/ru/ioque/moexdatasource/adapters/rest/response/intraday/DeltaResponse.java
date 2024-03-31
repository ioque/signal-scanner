package ru.ioque.moexdatasource.adapters.rest.response.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.intraday.Delta;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeltaResponse extends IntradayValueResponse {

    @Builder
    public DeltaResponse(Long tradeNumber, LocalDateTime dateTime, String ticker, Double value, Double price) {
        super(tradeNumber, dateTime, ticker, value, price);
    }

    public static DeltaResponse from(Delta intradayValue) {
        return DeltaResponse.builder()
            .tradeNumber(intradayValue.getTradeNumber())
            .ticker(intradayValue.getTicker())
            .dateTime(intradayValue.getDateTime())
            .price(intradayValue.getPrice())
            .value(intradayValue.getValue())
            .build();
    }
}
