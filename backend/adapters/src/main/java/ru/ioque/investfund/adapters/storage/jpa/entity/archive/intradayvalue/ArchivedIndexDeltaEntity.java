package ru.ioque.investfund.adapters.storage.jpa.entity.archive.intradayvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.Delta;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("IndexDeltaEntity")
public class ArchivedIndexDeltaEntity extends
    ArchivedIntradayValueEntity {
    Double value;

    @Builder
    public ArchivedIndexDeltaEntity(
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        super(dateTime, ticker, price);
        this.value = value;
    }

    @Override
    public IntradayValue toDomain() {
        return Delta.builder()
            .dateTime(dateTime)
            .ticker(ticker)
            .price(price)
            .value(value)
            .build();
    }

    public static ArchivedIntradayValueEntity from(Delta domain) {
        return ArchivedIndexDeltaEntity.builder()
            .dateTime(domain.getDateTime())
            .ticker(domain.getTicker())
            .price(domain.getPrice())
            .value(domain.getValue())
            .build();
    }
}
