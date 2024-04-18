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
import ru.ioque.investfund.domain.datasource.entity.Futures;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

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
        return Futures.builder()
            .id(this.getId())
            .ticker(this.getTicker())
            .name(this.getName())
            .shortName(this.getShortName())
            .updatable(this.getUpdatable())
            .lotVolume(this.getLotVolume())
            .initialMargin(this.getInitialMargin())
            .highLimit(this.getHighLimit())
            .lowLimit(this.getLowLimit())
            .assetCode(this.getAssetCode())
            .lastHistoryDate(this.getLastHistoryDate())
            .lastTradingNumber(this.getLastTradingNumber())
            .build();
    }

    public static InstrumentEntity from(Futures domain) {
        return FuturesEntity.builder()
            .id(domain.getId())
            .ticker(domain.getTicker())
            .name(domain.getName())
            .shortName(domain.getShortName())
            .updatable(domain.getUpdatable())
            .lotVolume(domain.getLotVolume())
            .initialMargin(domain.getInitialMargin())
            .highLimit(domain.getHighLimit())
            .lowLimit(domain.getLowLimit())
            .assetCode(domain.getAssetCode())
            .lastHistoryDate(domain.getLastHistoryDate().orElse(null))
            .lastTradingNumber(domain.getLastTradingNumber())
            .build();
    }
}
