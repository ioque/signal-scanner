package ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.util.List;
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
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode
    ) {
        super(id, ticker, shortName, name, updatable);
        this.lotVolume = lotVolume;
        this.initialMargin = initialMargin;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.assetCode = assetCode;
    }

    @Override
    public Instrument toDomain(
        List<HistoryValue> historyValues,
        List<IntradayValue> intradayValues
    ) {
        return Futures.builder()
            .id(this.getId())
            .ticker(this.getTicker())
            .name(this.getName())
            .shortName(this.getShortName())
            .lotVolume(this.getLotVolume())
            .initialMargin(this.getInitialMargin())
            .highLimit(this.getHighLimit())
            .lowLimit(this.getLowLimit())
            .assetCode(this.getAssetCode())
            .historyValues(historyValues)
            .intradayValues(intradayValues)
            .build();
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
            .build();
    }
}
