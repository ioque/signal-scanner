package ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue;

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
public class DeltaEntity extends IntradayValueEntity {

    @Builder
    public DeltaEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        super(number, dateTime, ticker, price, value);
    }

    @Override
    public IntradayData toDomain() {
        return Delta.builder()
            .ticker(Ticker.from(getId().getTicker()))
            .number(getId().getNumber())
            .price(price)
            .value(value)
            .build();
    }

    public static IntradayValueEntity from(Delta delta) {
        return DeltaEntity.builder()
            .ticker(delta.getTicker().getValue())
            .number(delta.getNumber())
            .price(delta.getPrice())
            .value(delta.getValue())
            .build();
    }
}
