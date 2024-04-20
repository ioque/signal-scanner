package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.FuturesDetails;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("FuturesEntity")
public class FuturesEntity extends InstrumentEntity {
    Integer lotVolume;
    Double initialMargin;
    Double highLimit;
    Double lowLimit;
    String assetCode;

    @Builder
    public FuturesEntity(
        UUID id,
        DatasourceEntity datasource,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id, datasource, ticker, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
        this.lotVolume = lotVolume;
        this.initialMargin = initialMargin;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.assetCode = assetCode;
    }

    @Override
    public Instrument toDomain() {
        return Instrument.builder()
            .id(InstrumentId.from(getId()))
            .details(
                FuturesDetails.builder()
                    .ticker(Ticker.from(this.getTicker()))
                    .name(this.getName())
                    .shortName(this.getShortName())
                    .lotVolume(this.getLotVolume())
                    .initialMargin(this.getInitialMargin())
                    .highLimit(this.getHighLimit())
                    .lowLimit(this.getLowLimit())
                    .assetCode(this.getAssetCode())
                    .build()
            )
            .updatable(this.getUpdatable())
            .lastHistoryDate(this.getLastHistoryDate())
            .lastTradingNumber(this.getLastTradingNumber())
            .build();
    }
}
