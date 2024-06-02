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
public class Deal extends IntradayData {
    @NotNull(message = "Не заполнен признак покупки.")
    Boolean isBuy;
    @NotNull(message = "Не заполнено количество лотов.")
    @Min(value = 0, message = "Количество лотов должно быть больше нуля.")
    Integer qnt;

    @Builder
    public Deal(
        InstrumentId instrumentId,
        Ticker ticker,
        Long number,
        Instant timestamp,
        Double price,
        Boolean isBuy,
        Integer qnt,
        Double value
    ) {
        super(instrumentId, ticker, number, timestamp, price, value);
        this.isBuy = isBuy;
        this.qnt = qnt;
    }
}
