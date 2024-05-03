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
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractDto extends IntradayDataDto {
    @NotNull(message = "Не заполнено количество контрактов.")
    @Min(value = 0, message = "Количество контрактов должно быть больше нуля.")
    Integer qnt;

    @Builder
    public ContractDto(
        String ticker,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(ticker, number, dateTime, price, value);
        this.qnt = qnt;
    }

    @Override
    public IntradayData toIntradayValue() {
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
