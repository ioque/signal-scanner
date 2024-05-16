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
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetail;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("Futures")
public class FuturesDetailsEntity extends InstrumentDetailsEntity {
    Integer lotVolume;

    Double initialMargin;

    Double highLimit;

    Double lowLimit;

    String assetCode;

    @Builder
    public FuturesDetailsEntity(
        InstrumentEntity instrument,
        String shortName,
        String name,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode
    ) {
        super(instrument, shortName, name);
        this.lotVolume = lotVolume;
        this.initialMargin = initialMargin;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.assetCode = assetCode;
    }

    @Override
    public InstrumentDetail toDomain() {
        return FuturesDetail.builder()
            .ticker(Ticker.from(instrument.getTicker()))
            .name(getName())
            .shortName(getShortName())
            .lotVolume(getLotVolume())
            .initialMargin(getInitialMargin())
            .highLimit(getHighLimit())
            .lowLimit(getLowLimit())
            .assetCode(getAssetCode())
            .build();
    }
}
