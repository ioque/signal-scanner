package ru.ioque.investfund.adapters.datasource.client.dto.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeltaDto extends IntradayValueDto {
    @Builder
    public DeltaDto(Long number, LocalDateTime dateTime, String ticker, Double value, Double price) {
        super(number, dateTime, ticker, value, price);
    }

    @Override
    public IntradayValue toIntradayValue() {
        return Delta.builder()
            .ticker(Ticker.from(getTicker()))
            .number(getNumber())
            .dateTime(getDateTime())
            .value(getValue())
            .price(getPrice())
            .build();
    }
}
