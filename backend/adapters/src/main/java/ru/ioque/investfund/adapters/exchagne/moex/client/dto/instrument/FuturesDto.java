package ru.ioque.investfund.adapters.exchagne.moex.client.dto.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FuturesDto extends InstrumentDto {
    Integer lotVolume;
    Double initialMargin;
    Double highLimit;
    Double lowLimit;
    String assetCode;

    @Builder
    public FuturesDto(
        String ticker,
        String shortName,
        String name,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode
    ) {
        super(ticker, shortName, name);
        this.lotVolume = lotVolume;
        this.initialMargin = initialMargin;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.assetCode = assetCode;
    }

    @Override
    public Instrument toDomain(UUID id) {
        return Futures.builder()
            .id(id)
            .ticker(getTicker())
            .name(getName())
            .shortName(getShortName())
            .lotVolume(lotVolume)
            .initialMargin(initialMargin)
            .highLimit(highLimit)
            .lowLimit(lowLimit)
            .assetCode(assetCode)
            .historyValues(new ArrayList<>())
            .intradayValues(new ArrayList<>())
            .build();
    }
}
