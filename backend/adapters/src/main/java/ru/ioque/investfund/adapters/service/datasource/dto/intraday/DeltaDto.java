package ru.ioque.investfund.adapters.service.datasource.dto.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeltaDto extends IntradayDataDto {
    @Builder
    public DeltaDto(
        String ticker,
        Long number,
        LocalDateTime timestamp,
        Double price,
        Double value
    ) {
        super(ticker, number, timestamp, price, value);
    }

    @Override
    public IntradayData toIntradayData(InstrumentId instrumentId) {
        return Delta.builder()
            .instrumentId(instrumentId)
            .ticker(Ticker.from(getTicker()))
            .number(getNumber())
            .timestamp(getDateTime().atZone(ZoneId.of("Europe/Moscow")).toInstant())
            .value(getValue())
            .price(getPrice())
            .build();
    }
}
