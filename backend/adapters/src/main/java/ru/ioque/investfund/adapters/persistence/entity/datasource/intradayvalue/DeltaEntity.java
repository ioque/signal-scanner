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
    public IntradayValue toDomain() {
        return Delta.builder()
            .number(number)
            .dateTime(dateTime)
            .ticker(ticker)
            .price(price)
            .value(value)
            .build();
    }

    public static IntradayValueEntity from(Delta domain) {
        return DeltaEntity.builder()
            .number(domain.getNumber())
            .dateTime(domain.getDateTime())
            .ticker(domain.getTicker())
            .price(domain.getPrice())
            .value(domain.getValue())
            .build();
    }
}
