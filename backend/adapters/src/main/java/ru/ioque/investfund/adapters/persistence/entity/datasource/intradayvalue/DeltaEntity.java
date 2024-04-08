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
import ru.ioque.investfund.domain.datasource.value.Delta;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDateTime;
import java.util.UUID;

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
        UUID datasourceId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        super(datasourceId, number, dateTime, ticker, price, value);
    }

    @Override
    public IntradayValue toDomain() {
        return Delta.builder()
            .datasourceId(datasourceId)
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .price(price)
            .value(value)
            .build();
    }

    public static IntradayValueEntity from(Delta delta) {
        return DeltaEntity.builder()
            .datasourceId(delta.getDatasourceId())
            .number(delta.getNumber())
            .dateTime(delta.getDateTime())
            .ticker(delta.getTicker())
            .price(delta.getPrice())
            .value(delta.getValue())
            .build();
    }
}
