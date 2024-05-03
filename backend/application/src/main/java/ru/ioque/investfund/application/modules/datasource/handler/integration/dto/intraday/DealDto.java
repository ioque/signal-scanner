package ru.ioque.investfund.application.modules.datasource.handler.integration.dto.intraday;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DealDto extends IntradayDataDto {
    @NotNull(message = "Не заполнен признак покупки.")
    Boolean isBuy;
    @NotNull(message = "Не заполнено количество лотов.")
    @Min(value = 0, message = "Количество лотов должно быть больше нуля.")
    Integer qnt;

    @Builder
    public DealDto(
        String ticker,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Double value,
        Integer qnt,
        Boolean isBuy
    ) {
        super(ticker, number, dateTime, price, value);
        this.qnt = qnt;
        this.isBuy = isBuy;
    }

    @Override
    public IntradayData toIntradayValue() {
        return Delta.builder()
            .ticker(Ticker.from(getTicker()))
            .number(getNumber())
            .dateTime(getDateTime())
            .value(getValue())
            .price(getPrice())
            .build();
    }
}
