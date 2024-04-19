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
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.Delta;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;
import ru.ioque.investfund.domain.datasource.value.Ticker;

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
        Long id,
        UUID datasourceId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        super(id, datasourceId, number, dateTime, ticker, price, value);
    }

    @Override
    public IntradayValue toDomain() {
        return Delta.builder()
            .datasourceId(DatasourceId.from(datasourceId))
            .instrumentId(InstrumentId.from(Ticker.from(ticker)))
            .number(number)
            .dateTime(dateTime)
            .price(price)
            .value(value)
            .build();
    }

    public static IntradayValueEntity from(Delta delta) {
        return DeltaEntity.builder()
            .datasourceId(delta.getDatasourceId().getUuid())
            .ticker(delta.getInstrumentId().getTicker().getValue())
            .number(delta.getNumber())
            .dateTime(delta.getDateTime())
            .price(delta.getPrice())
            .value(delta.getValue())
            .build();
    }
}
