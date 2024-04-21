package ru.ioque.investfund.adapters.persistence.entity.archive.intradayvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("IndexDeltaEntity")
public class ArchivedIndexDelta extends ArchivedIntradayValue {

    @Builder
    public ArchivedIndexDelta(
        Long id,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        super(id, number, dateTime, ticker, price, value);
    }

    @Override
    public IntradayData toDomain() {
        return Delta.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .dateTime(dateTime)
            .price(price)
            .value(value)
            .build();
    }

    public static ArchivedIntradayValue from(Delta delta) {
        return ArchivedIndexDelta.builder()
            .ticker(delta.getTicker().getValue())
            .number(delta.getNumber())
            .dateTime(delta.getDateTime())
            .price(delta.getPrice())
            .value(delta.getValue())
            .build();
    }
}
