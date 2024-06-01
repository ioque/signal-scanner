package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.details;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetail;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("CurrencyPair")
public class CurrencyPairDetailsEntity extends InstrumentDetailsEntity {

    Integer lotSize;

    String faceUnit;

    @Builder
    public CurrencyPairDetailsEntity(
        InstrumentEntity instrument,
        String ticker,
        String shortName,
        String name,
        Integer lotSize,
        String faceUnit
    ) {
        super(instrument, ticker, shortName, name);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    @Override
    public InstrumentDetail toDomain() {
        return CurrencyPairDetail.builder()
            .ticker(Ticker.from(getTicker()))
            .name(getName())
            .shortName(getShortName())
            .lotSize(getLotSize())
            .faceUnit(getFaceUnit())
            .build();
    }
}
