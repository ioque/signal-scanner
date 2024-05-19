package ru.ioque.investfund.domain.datasource.value.intraday;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract extends IntradayData {
    @NotNull(message = "Не заполнено количество контрактов.")
    @Min(value = 0, message = "Количество контрактов должно быть больше нуля.")
    Integer qnt;

    @Builder
    public Contract(
        Ticker ticker,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(ticker, number, dateTime, price, value);
        this.qnt = qnt;
    }
}
