package ru.ioque.investfund.adapters.psql.entity.datasource.intradayvalue;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("IndexDeltaEntity")
public class DeltaEntity extends IntradayDataEntity {

    @Builder
    public DeltaEntity(
        Long number,
        UUID instrumentId,
        String ticker,
        Instant timestamp,
        Double price,
        Double value
    ) {
        super(number, instrumentId, ticker, timestamp, price, value);
    }

    @Override
    public IntradayData toDomain() {
        return Delta.builder()
            .instrumentId(InstrumentId.from(getId().getInstrumentId()))
            .ticker(Ticker.from(getTicker()))
            .number(getId().getNumber())
            .price(price)
            .value(value)
            .timestamp(getTimestamp())
            .build();
    }

    public static IntradayDataEntity from(Delta delta) {
        return DeltaEntity.builder()
            .instrumentId(delta.getInstrumentId().getUuid())
            .ticker(delta.getTicker().getValue())
            .number(delta.getNumber())
            .price(delta.getPrice())
            .value(delta.getValue())
            .timestamp(delta.getTimestamp())
            .build();
    }
}
