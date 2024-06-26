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
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.Instant;

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
        InstrumentId instrumentId,
        Ticker ticker,
        Long number,
        Instant timestamp,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(instrumentId, ticker, number, timestamp, price, value);
        this.qnt = qnt;
    }
}
